
//
// Created by GHr_Ryuuka on 05/12/2021.
//

#include "Includes/obfuscate.h"
#include "Il2cpp.h"
#include <iostream> 
#include <unordered_map>

#define targetLib OBFUSCATE("libil2cpp.so")
#define g_LogTag OBFUSCATE("IL2CPP-LOGGER")

typedef unsigned short UTF16;
typedef wchar_t UTF32;
typedef char UTF8;

namespace {
	Il2CppString *(*il2cpp_string_new)(const char *str);
    Il2CppString *(*il2cpp_string_new_utf16)(const wchar_t *str, int32_t length);
	void **(*il2cpp_domain_get_assemblies)(const void *domain, size_t *size);
	void *(*il2cpp_domain_get)();
	const void *(*il2cpp_assembly_get_image)(const void *assembly);
	const char *(*il2cpp_image_get_name)(void *image);
    void *(*il2cpp_class_from_name)(const void *image, const char *namespaze, const char *name);
    void *(*il2cpp_class_get_field_from_name)(void *klass, const char *name);
	void (*il2cpp_field_static_get_value)(void *field, void *value);
    void (*il2cpp_field_static_set_value)(void *field, void *value);
	void *(*il2cpp_class_get_method_from_name)(void *klass, const char *name, int argsCount);
	size_t (*il2cpp_field_get_offset)(void *field);
}

void *IL2Cpp::Il2CppGetImageByName(const char *image) {
    size_t size;

    void **assemblies = il2cpp_domain_get_assemblies(il2cpp_domain_get(), &size);

    for (int i = 0; i < size; ++i) {
        void *img = (void *)il2cpp_assembly_get_image(assemblies[i]);
        const char *img_name = il2cpp_image_get_name(img);

        if (strcmp(img_name, image) == 0) {
            return img;
        }
    }

    return nullptr;
}

void *IL2Cpp::Il2CppGetClassType(const char *image, const char *namespaze, const char *clazz) {
    static std::unordered_map<std::string, void*> cache;
    
    const std::string key = std::string(image) + namespaze + clazz;
    auto it = cache.find(key);
    if (it != cache.end()) {
        return it->second;
    }

    void *img = Il2CppGetImageByName(image);
    if (!img) {
        __android_log_print(ANDROID_LOG_ERROR, g_LogTag, OBFUSCATE("Can't find image %s!"), image);
        return nullptr;
    }

    void *klass = il2cpp_class_from_name(img, namespaze, clazz);
    if (!klass) {
        __android_log_print(ANDROID_LOG_ERROR, g_LogTag, OBFUSCATE("Can't find class %s!"), clazz);
        return nullptr;
    }

    cache[key] = klass;
    return klass;
}

struct StaticFieldInfo {
    void* klass;
    void* field;
};

void IL2Cpp::Il2CppGetStaticFieldValue(const char *image, const char *namespaze, const char *clazz, const char *name, void *output) {
    void *img = IL2Cpp::Il2CppGetImageByName(image);
    if (!img) {
        __android_log_print(ANDROID_LOG_ERROR, g_LogTag, OBFUSCATE("Can't find image %s!"), image);
        return;
    }

    StaticFieldInfo fieldInfo;
    fieldInfo.klass = IL2Cpp::Il2CppGetClassType(image, namespaze, clazz);
    if (!fieldInfo.klass) {
        __android_log_print(ANDROID_LOG_ERROR, g_LogTag, OBFUSCATE("Can't find class %s!"), clazz);
        return;
    }

    fieldInfo.field = il2cpp_class_get_field_from_name(fieldInfo.klass, name);
    if (!fieldInfo.field) {
        __android_log_print(ANDROID_LOG_ERROR, g_LogTag, OBFUSCATE("Can't find field %s in class %s!"), name, clazz);
        return;
    }

    il2cpp_field_static_get_value(fieldInfo.field, output);
}

void IL2Cpp::Il2CppSetStaticFieldValue(const char *image, const char *namespaze, const char *clazz, const char *name, void* value) {
    void *img = IL2Cpp::Il2CppGetImageByName(image);
    if (!img) {
        __android_log_print(ANDROID_LOG_ERROR, g_LogTag, OBFUSCATE("Can't find image %s!"), image);
        return;
    }

    StaticFieldInfo fieldInfo;
    fieldInfo.klass = IL2Cpp::Il2CppGetClassType(image, namespaze, clazz);
    if (!fieldInfo.klass) {
        __android_log_print(ANDROID_LOG_ERROR, g_LogTag, OBFUSCATE("Can't find class %s!"), clazz);
        return;
    }

    fieldInfo.field = il2cpp_class_get_field_from_name(fieldInfo.klass, name);
    if (!fieldInfo.field) {
        __android_log_print(ANDROID_LOG_ERROR, g_LogTag, OBFUSCATE("Can't find field %s in class %s!"), name, clazz);
        return;
    }

    il2cpp_field_static_set_value(fieldInfo.field, value);
}

void *IL2Cpp::Il2CppGetMethodOffset(const char *image, const char *namespaze, const char *clazz, const char *name, int argsCount) {
	void *img = IL2Cpp::Il2CppGetImageByName(image);
	if (!img) {
		__android_log_print(ANDROID_LOG_ERROR, g_LogTag, OBFUSCATE("Can't find image %s!"), image);
		return nullptr;
	}
	void *klass = IL2Cpp::Il2CppGetClassType(image, namespaze, clazz);
	if (!klass) {
		__android_log_print(ANDROID_LOG_ERROR, g_LogTag, OBFUSCATE("Can't find method %s!"), clazz, name);
		return nullptr;
	}
	void **method = (void**)il2cpp_class_get_method_from_name(klass, name, argsCount);
	if (!method) {
		__android_log_print(ANDROID_LOG_ERROR, g_LogTag, OBFUSCATE("Can't find method %s in class %s!"), name, clazz);
		return nullptr;
	}
	__android_log_print(ANDROID_LOG_DEBUG, g_LogTag, OBFUSCATE("%s - [%s] %s::%s: %p"), image, namespaze, clazz, name, *method);
	return *method;
}

size_t IL2Cpp::Il2CppGetFieldOffset(const char * const image, const char * const namespaze, const char * const clazz, const char * const name) {
    auto * const img = IL2Cpp::Il2CppGetImageByName(image);
    if (!img) {
        __android_log_print(ANDROID_LOG_ERROR, g_LogTag, OBFUSCATE("Can't find image %s!"), image);
        return -1;
    }

    auto * const klass = IL2Cpp::Il2CppGetClassType(image, namespaze, clazz);
    if (!klass) {
        __android_log_print(ANDROID_LOG_ERROR, g_LogTag, OBFUSCATE("Can't find field %s!"), clazz, name);
        return -1;
    }

    auto * const field = il2cpp_class_get_field_from_name(klass, name);
    if (!field) {
        __android_log_print(ANDROID_LOG_ERROR, g_LogTag, OBFUSCATE("Can't find field %s in class %s!"), clazz, name);
        return -1;
    }

    const auto result = il2cpp_field_get_offset(field);
    if (result == -1) {
        __android_log_print(ANDROID_LOG_ERROR, g_LogTag, OBFUSCATE("Failed to get offset of field %s in class %s!"), name, clazz);
        return -1;
    }

    __android_log_print(ANDROID_LOG_DEBUG, g_LogTag, OBFUSCATE("%s - [%s] %s::%s: %p"), image, namespaze, clazz, name, reinterpret_cast<void*>(result));
    return result;
}


int is_surrogate(UTF16 uc) {
    return (uc - 0xd800u) < 2048u;
}

int is_high_surrogate(UTF16 uc) {
    return (uc & 0xfffffc00) == 0xd800;
}

int is_low_surrogate(UTF16 uc) {
    return (uc & 0xfffffc00) == 0xdc00;
}

UTF32 surrogate_to_utf32(UTF16 high, UTF16 low) {
    return (high << 10) + low - 0x35fdc00;
}

const char* utf16_to_utf8(const UTF16* source, size_t len) {
    std::u16string s(source, source + len);
    std::wstring_convert<std::codecvt_utf8_utf16<char16_t>, char16_t> convert;
    return convert.to_bytes(s).c_str();
}

const wchar_t* utf16_to_utf32(const UTF16* source, size_t len) {
	auto output = new UTF32[len + 1];

    for (int i = 0; i < len; i++) {
        const UTF16 uc = source[i];
        if (!is_surrogate(uc)) {
            output[i] = uc;
        }
        else {
            if (is_high_surrogate(uc) && is_low_surrogate(source[i]))
                output[i] = surrogate_to_utf32(uc, source[i]);
            else
                output[i] = L'?';
        }
    }

    output[len] = L'\0';
    return output;
}

const char* Il2CppString::CString() {
    return utf16_to_utf8(&this->start_char, this->length);
}

const wchar_t* Il2CppString::WCString() {
    return utf16_to_utf32(&this->start_char, this->length);
}

Il2CppString *Il2CppString::Create(const char *s) {
    return il2cpp_string_new(s);
}

Il2CppString *Il2CppString::Create(const wchar_t *s, int len) {
    return il2cpp_string_new_utf16(s, len);
}

void IL2Cpp::Il2CppAttach() {
	void *IL2Cpp_Handle = nullptr;
	
	while (!IL2Cpp_Handle) {
		IL2Cpp_Handle = dlopen(targetLib, 4);
		sleep(1);
	}
	
	il2cpp_domain_get_assemblies = (void **(*)(const void *, size_t *)) dlsym(IL2Cpp_Handle, OBFUSCATE("il2cpp_domain_get_assemblies"));
	il2cpp_string_new = (Il2CppString *(*)(const char *)) dlsym(IL2Cpp_Handle, OBFUSCATE("il2cpp_string_new"));
    il2cpp_string_new_utf16 = (Il2CppString *(*)(const wchar_t *, int32_t)) dlsym(IL2Cpp_Handle, OBFUSCATE("il2cpp_string_new_utf16"));
	il2cpp_domain_get = (void *(*)()) dlsym(IL2Cpp_Handle, OBFUSCATE("il2cpp_domain_get"));
    il2cpp_assembly_get_image = (const void *(*)(const void *)) dlsym(IL2Cpp_Handle, OBFUSCATE("il2cpp_assembly_get_image"));
	il2cpp_image_get_name = (const char *(*)(void *)) dlsym(IL2Cpp_Handle, OBFUSCATE("il2cpp_image_get_name"));
    il2cpp_class_from_name = (void* (*)(const void*, const char*, const char *)) dlsym(IL2Cpp_Handle, OBFUSCATE("il2cpp_class_from_name"));
    il2cpp_class_get_field_from_name = (void* (*)(void*, const char *)) dlsym(IL2Cpp_Handle, OBFUSCATE("il2cpp_class_get_field_from_name"));
    il2cpp_field_static_get_value = (void (*)(void*, void *)) dlsym(IL2Cpp_Handle, OBFUSCATE("il2cpp_field_static_get_value"));
	il2cpp_field_static_set_value = (void (*)(void*, void *)) dlsym(IL2Cpp_Handle, OBFUSCATE("il2cpp_field_static_set_value"));
    il2cpp_class_get_method_from_name = (void* (*)(void *, const char*, int)) dlsym(IL2Cpp_Handle, OBFUSCATE("il2cpp_class_get_method_from_name"));
    il2cpp_field_get_offset = (size_t (*)(void *)) dlsym(IL2Cpp_Handle, OBFUSCATE("il2cpp_field_get_offset"));
	dlclose(IL2Cpp_Handle);
}
