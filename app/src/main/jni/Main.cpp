#include <list>
#include <vector>
#include <string.h>
#include <pthread.h>
#include <thread>
#include <cstring>
#include <jni.h>
#include <unistd.h>
#include <fstream>
#include <iostream>
#include <dlfcn.h>
#include "Includes/Logger.h"
#include "Includes/obfuscate.h"
#include "Includes/Utils.h"
#include "KittyMemory/MemoryPatch.h"
#include "Menu/Setup.h"
#include "TuanMeta/Call_Me.h"
#include "Unity/MonoString.h"
#include "Unity/Quaternion.hpp"
#include "Unity/Vector3.hpp"
#include "Unity/Color.hpp"
#include "Unity/Vector2.hpp"
#include "Unity/VInt3.hpp"
#include "Unity/Unity.h"
#include "Unity/Rect.hpp"
#include "Unity/Esp.h"
#include "Unity/EspManager.h"
#include "Unity/Draw.h"
#include "Unity/Alert.h"
#define targetLibName OBFUSCATE("libil2cpp.so")
//#define targetLibName OBFUSCATE("libanogs.so")
#include "Includes/Macros.h"

struct My_Patches {
    // let's assume we have patches for these functions for whatever game
    // like show in miniMap boolean function
MemoryPatch SliderExample,hackmap1,hackmap2,hackmap3,camcao,showteamhp1,showteamhp2,aimalltuong,botro1,botro2,botro3,fps120,ispad,showlichsu,showname,aim,aim1,aim2,aim3,hackhoichieu;
} hexPatches;

Color EspColor = Color::White();
int BoxWidth = 100;
int BoxHeight = 160;
int SizeLine = 1;
bool check_move = false;
int zoom = 30, zoom1 =30,zoom2=30,zoom3=30,zoom4=30;
bool bEsp, map, ult, bEspCountEnemy, bEspLine, bEspBox, bEspHp, bEspEnemyHealth, bEspEnemyDistance, bEspAlert, bAneko,cccc, cccc1,cccc2,cccc3,cccc4,feature1,feature2,feature3,feature4,feature5,feature6,feature7,feature8,feature9,feature10,feature11;
ESPManager *espManager;
ESPManager *ActorLinker_enemy;
int sliderValue = 1;
void *instanceBtn;
void* (*Camera_get_main)(void *instance);
Vector3 (*Camera_WorldToScreenPoint)(void *instance, Vector3 position);

VInt3 (*LActorRoot_get_location)(void *instance);
VInt3 (*LActorRoot_get_forward)(void *instance);
void* (*LActorRoot_LHeroWrapper)(void *instance);
int (*LActorRoot_COM_PLAYERCAMP)(void *instance);
bool (*LActorRoot_get_bActive)(void *instance);
int (*LActorRoot_get_ObjID)(void *instance);

bool (*LObjWrapper_get_IsDeadState)(void *instance);

int (*ValuePropertyComponent_get_actorHp)(void *instance);
int (*ValuePropertyComponent_get_actorHpTotal)(void *instance);
void* (*ValuePropertyComponent_BaseEnergyLogic)(void *instance);

int (*ActorLinker_COM_PLAYERCAMP)(void *instance);
bool (*ActorLinker_IsHostPlayer)(void *instance);
int (*ActorLinker_ActorTypeDef)(void *instance);
Vector3 (*ActorLinker_getPosition)(void *instance);
bool (*ActorLinker_get_HPBarVisible)(void *instance);
int (*ActorLinker_get_ObjID)(void *instance);
bool (*ActorLinker_get_bVisible)(void *instance);

void (*old_ActorLinker_ActorDestroy)(void *instance);
void ActorLinker_ActorDestroy(void *instance) {
    if (instance != NULL) {
        old_ActorLinker_ActorDestroy(instance);
        if (espManager->MyPlayer==instance){
            espManager->MyPlayer=NULL;
        }
    }
}

void (*old_LActorRoot_ActorDestroy)(void *instance,bool bTriggerEvent);
void LActorRoot_ActorDestroy(void *instance, bool bTriggerEvent) {
    if (instance != NULL) {
        old_LActorRoot_ActorDestroy(instance, bTriggerEvent);
        espManager->removeEnemyGivenObject(instance);
        
    }
}
void (*ByPassEsp)(void *player);
void ByPassesp(void *player) {
    if(player != NULL) {
        return;
    }
    ByPassEsp(player);
}
void (*old_ActorLinker_Update)(void *instance);
void ActorLinker_Update(void *instance) {
    if (instance != NULL) {
        old_ActorLinker_Update(instance);
       // int id1 = ActorLinker_get_actorHpTotal(instance);
        // LOGD(OBFUSCATE("id2: %d"),id1);
        if (ActorLinker_ActorTypeDef(instance)==0){
            if (ActorLinker_IsHostPlayer(instance)==true){
                espManager->tryAddMyPlayer(instance);
            } else ActorLinker_enemy->tryAddEnemy(instance);
        }
    }
}

void (*old_LActorRoot_UpdateLogic)(void *instance, int delta);
void LActorRoot_UpdateLogic(void *instance, int delta) {
    if (instance != NULL) {
        old_LActorRoot_UpdateLogic(instance, delta);
       // int id2 = ActorLinker_get_actorHpTotal1(instance);
        // LOGD(OBFUSCATE("id1: %d"),id2);
        if (espManager->MyPlayer!=NULL)
            if (LActorRoot_LHeroWrapper(instance)!=NULL && LActorRoot_COM_PLAYERCAMP(instance) == ActorLinker_COM_PLAYERCAMP(espManager->MyPlayer)) 
                espManager->tryAddEnemy(instance);
    }
}


int dem(int num){
    int div=1, num1 = num;
    while (num1!=0){
        num1=num1/10;
        div=div*10;
    }
    return div;
}

Vector3 VInt2Vector(VInt3 location, VInt3 forward){
    return Vector3((float)(location.X*dem(forward.X)+forward.X)/(1000*dem(forward.X)), (float)(location.Y*dem(forward.Y)+forward.Y)/(1000*dem(forward.Y)), (float)(location.Z*dem(forward.Z)+forward.Z)/(1000*dem(forward.Z)));
}


bool (*old_SetVisible)(...);
bool hook_SetVisible(void *instance,int camp, bool bVisible, const bool forceSync = false) {                                                                                
    if (instance && feature1) {
            bVisible = true;
        }
    
   return old_SetVisible(instance,camp, bVisible, forceSync);
}

bool (*old_get_BoolExample)(void *instance);
bool get_BoolExample(void *instance) {
    if (instance != NULL && feature3) {
        return true;
    }
    return old_get_BoolExample(instance);
}

float (*old_FloatExample)(void *instance, int *type);
float FloatExample(void *instance, int *type) {
    if (instance != NULL && sliderValue > 1) {
        return (float) sliderValue;
    }
}

bool (*_IsHostProfile)(void *instance);
bool IsHostProfile(void *instance) {
    if (instance != nullptr && feature8) {
        return true;
    }
    return _IsHostProfile(instance);
}

bool *(*_InitTeamHeroList) (...);
bool InitTeamHeroList(void* instance, void *listScript, int camp, bool isLeftList, const bool isMidPos = false) {
	if (instance != nullptr && feature9) {
		isLeftList = true;
	}
	return _InitTeamHeroList(instance, listScript, camp, isLeftList, isMidPos);
	}

void *hack_thread(void *) {
	
    espManager = new ESPManager();
    ActorLinker_enemy = new ESPManager();
    
    sleep(5);
    
    ProcMap il2cppMap;
    do {
        il2cppMap = KittyMemory::getLibraryMap("libil2cpp.so");
        sleep(1);
    } while (!il2cppMap.isValid());
    
    
    do {
        sleep(1);

    } while (!isLibraryLoaded("libanogs.so"));

    LOGI(OBFUSCATE("%s has been loaded"), (const char *) targetLibName);
    #if defined(__aarch64__) 
    #else //To compile this code for armv7 lib only.

    IL2Cpp::Il2CppAttach();
    Tools::Hook((void *) (uintptr_t)IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("Project_d.dll"), OBFUSCATE("Kyrios.Actor"), OBFUSCATE("ActorLinker") , OBFUSCATE("Update"), 0), (void *) ActorLinker_Update, (void **) &old_ActorLinker_Update);
    
    Tools::Hook((void *) (uintptr_t)IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("Project.Plugins_d.dll"), OBFUSCATE("NucleusDrive.Logic"), OBFUSCATE("LActorRoot") , OBFUSCATE("UpdateLogic"), 1), (void *) LActorRoot_UpdateLogic, (void **) &old_LActorRoot_UpdateLogic);
    
    Tools::Hook((void *) (uintptr_t)IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("Project_d.dll"), OBFUSCATE("Kyrios.Actor"), OBFUSCATE("ActorLinker") , OBFUSCATE("DestroyActor"), 0), (void *) ActorLinker_ActorDestroy, (void **) &old_ActorLinker_ActorDestroy);
    
    Tools::Hook((void *) (uintptr_t)IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("Project.Plugins_d.dll"), OBFUSCATE("NucleusDrive.Logic"), OBFUSCATE("LActorRoot") , OBFUSCATE("DestroyActor"), 1), (void *) LActorRoot_ActorDestroy, (void **) &old_LActorRoot_ActorDestroy);

    Tools::Hook((void *) (uintptr_t)IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("UnityEngine.CoreModule.dll"), OBFUSCATE("UnityEngine"), OBFUSCATE("Screen") , OBFUSCATE("SetResolution"), 3), (void *) ByPassesp, (void **) &ByPassEsp);
    
    Camera_get_main = (void* (*)(void *))IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("UnityEngine.CoreModule.dll"), OBFUSCATE("UnityEngine"), OBFUSCATE("Camera") , OBFUSCATE("get_main"), 0);
    
    
    Camera_WorldToScreenPoint = (Vector3 (*)(void *, Vector3))IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("UnityEngine.CoreModule.dll"), OBFUSCATE("UnityEngine"), OBFUSCATE("Camera") , OBFUSCATE("WorldToScreenPoint"), 1);

    ActorLinker_IsHostPlayer = (bool (*)(void *)) IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("Project_d.dll"), OBFUSCATE("Kyrios.Actor"), OBFUSCATE("ActorLinker") , OBFUSCATE("IsHostPlayer"), 0);
    ActorLinker_ActorTypeDef = (int (*)(void *)) IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("Project_d.dll"), OBFUSCATE("Kyrios.Actor"), OBFUSCATE("ActorLinker") , OBFUSCATE("get_objType"), 0);
    ActorLinker_COM_PLAYERCAMP = (int (*)(void *)) IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("Project_d.dll"), OBFUSCATE("Kyrios.Actor"), OBFUSCATE("ActorLinker") , OBFUSCATE("get_objCamp"), 0);
    ActorLinker_getPosition = (Vector3 (*)(void *)) IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("Project_d.dll"), OBFUSCATE("Kyrios.Actor"), OBFUSCATE("ActorLinker") , OBFUSCATE("get_position"), 0);
    ActorLinker_get_HPBarVisible = (bool (*)(void *)) IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("Project_d.dll"), OBFUSCATE("Kyrios.Actor"), OBFUSCATE("ActorLinker") , OBFUSCATE("get_HPBarVisible"), 0);
    ActorLinker_get_ObjID = (int (*)(void *)) IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("Project_d.dll"), OBFUSCATE("Kyrios.Actor"), OBFUSCATE("ActorLinker") , OBFUSCATE("get_ObjID"), 0);
    ActorLinker_get_bVisible = (bool (*)(void *)) IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("Project_d.dll"), OBFUSCATE("Kyrios.Actor"), OBFUSCATE("ActorLinker") , OBFUSCATE("get_bVisible"), 0);


    LActorRoot_get_forward = (VInt3 (*)(void *)) IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("Project.Plugins_d.dll"), OBFUSCATE("NucleusDrive.Logic"), OBFUSCATE("LActorRoot") , OBFUSCATE("get_forward"), 0);
    LActorRoot_get_location = (VInt3 (*)(void *)) IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("Project.Plugins_d.dll"), OBFUSCATE("NucleusDrive.Logic"), OBFUSCATE("LActorRoot") , OBFUSCATE("get_location"), 0);
    LActorRoot_LHeroWrapper = (void* (*)(void *)) IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("Project.Plugins_d.dll"), OBFUSCATE("NucleusDrive.Logic"), OBFUSCATE("LActorRoot") , OBFUSCATE("AsHero"), 0);
    LActorRoot_COM_PLAYERCAMP = (int (*)(void *)) IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("Project.Plugins_d.dll"), OBFUSCATE("NucleusDrive.Logic"), OBFUSCATE("LActorRoot") , OBFUSCATE("GiveMyEnemyCamp"), 0);
    LActorRoot_get_bActive = (bool (*)(void *)) IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("Project.Plugins_d.dll"), OBFUSCATE("NucleusDrive.Logic"), OBFUSCATE("LActorRoot") , OBFUSCATE("get_bActive"), 0);
    LActorRoot_get_ObjID = (int (*)(void *)) IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("Project.Plugins_d.dll"), OBFUSCATE("NucleusDrive.Logic"), OBFUSCATE("LActorRoot") , OBFUSCATE("get_ObjID"), 0);

    LObjWrapper_get_IsDeadState = (bool (*)(void *)) IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("Project.Plugins_d.dll"), OBFUSCATE("NucleusDrive.Logic"), OBFUSCATE("LObjWrapper") , OBFUSCATE("get_IsDeadState"), 0);

    ValuePropertyComponent_get_actorHp = (int (*)(void *)) IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("Project.Plugins_d.dll"), OBFUSCATE("NucleusDrive.Logic"), OBFUSCATE("ValuePropertyComponent") , OBFUSCATE("get_actorHp"), 0);
    ValuePropertyComponent_get_actorHpTotal = (int (*)(void *)) IL2Cpp::Il2CppGetMethodOffset(OBFUSCATE("Project.Plugins_d.dll"), OBFUSCATE("NucleusDrive.Logic"), OBFUSCATE("ValuePropertyComponent") , OBFUSCATE("get_actorHpTotal"), 0);

    MemoryPatch::createWithHex("libanogs.so", 0x3AC, "1E FF 2F E1").Modify();
    MemoryPatch::createWithHex("libanogs.so", 0x11090, "00 46 70 47").Modify();
    MemoryPatch::createWithHex("libanogs.so", 0x124E0, "00 46 70 47").Modify();
    MemoryPatch::createWithHex("libanogs.so", 0x1C44C, "1E FF 2F E1").Modify();
    MemoryPatch::createWithHex("libanogs.so", 0x1C454, "00 00 00 00").Modify();
    MemoryPatch::createWithHex("libanogs.so", 0x1C45C, "1E FF 2F E1").Modify();
    MemoryPatch::createWithHex("libanogs.so", 0x1C474, "1E FF 2F E1").Modify();
    MemoryPatch::createWithHex("libanogs.so", 0x1E1550, "00 00 70 47").Modify();
    MemoryPatch::createWithHex("libanogs.so", 0x1EB150, "70 47 70 47").Modify();
    MemoryPatch::createWithHex("libanogs.so", 0x1EB154, "70 47 9A 42").Modify();   
    MemoryPatch::createWithHex("libanogs.so", 0x00FC42D8 , "00000000").Modify(); 
    MemoryPatch::createWithHex("libanogs.so", 0x00FC4EB4 , "00000000").Modify(); 
    MemoryPatch::createWithHex("libanogs.so", 0x00FC4FA8 , "00000000").Modify(); 
    MemoryPatch::createWithHex("libanogs.so", 0x00FD3138 , "00000000").Modify(); 
    MemoryPatch::createWithHex("libanogs.so", 0x00FD45C4 , "00000000").Modify(); 
    MemoryPatch::createWithHex("libanogs.so", 0x01EFBE34 , "00000000").Modify(); 
    MemoryPatch::createWithHex("libanogs.so", 0x01EFD378 , "00000000").Modify(); 

    MSHookFunction((void *)getAbsoluteAddress("libil2cpp.so", 0x1EED110), (void *) hook_SetVisible, (void **) &old_SetVisible); 
    hexPatches.hackmap1 = MemoryPatch::createWithHex(targetLibName,string2Offset(OBFUSCATE("0x1EED6CC")),"01 00 A0 E3 1E FF 2F E1");
    hexPatches.hackmap2 = MemoryPatch::createWithHex(targetLibName,string2Offset(OBFUSCATE("0x20D3DD0")),"01 00 A0 E3 1E FF 2F E1");
    hexPatches.hackmap3 = MemoryPatch::createWithHex(targetLibName,string2Offset(OBFUSCATE("0x1EEEA34")),"01 00 A0 E3 1E FF 2F E1");

    MSHookFunction((void *) getAbsoluteAddress("libil2cpp.so", 0x18EC200),(void *) FloatExample, (void **) &old_FloatExample);
    hexPatches.showteamhp1 = MemoryPatch::createWithHex(targetLibName,string2Offset(OBFUSCATE("0x174635C")),"01 00 A0 E3 1E FF 2F E1");
    hexPatches.showteamhp2 = MemoryPatch::createWithHex(targetLibName,string2Offset(OBFUSCATE("0x1B0DA74")),"01 00 A0 E3 1E FF 2F E1");
    hexPatches.aimalltuong = MemoryPatch::createWithHex(targetLibName,string2Offset(OBFUSCATE("0x1D70AF4")),"01 00 A0 E3 1E FF 2F E1");
    hexPatches.botro1 = MemoryPatch::createWithHex(targetLibName,string2Offset(OBFUSCATE("0x1363B80")),"01 00 A0 E3 1E FF 2F E1");
    hexPatches.botro2 = MemoryPatch::createWithHex(targetLibName,string2Offset(OBFUSCATE(" 0x1B0DA74")),"01 00 A0 E3 1E FF 2F E1");
    hexPatches.botro3 = MemoryPatch::createWithHex(targetLibName,string2Offset(OBFUSCATE("0x1B0DBC8")),"01 00 A0 E3 1E FF 2F E1");
    hexPatches.fps120 = MemoryPatch::createWithHex(targetLibName,string2Offset(OBFUSCATE("0xFC4EB4")),"01 00 A0 E3 1E FF 2F E1");
    hexPatches.ispad = MemoryPatch::createWithHex(targetLibName,string2Offset(OBFUSCATE("0xFC42D8 ")),"01 00 A0 E3 1E FF 2F E1");
	MSHookFunction((void *) getAbsoluteAddress("libil2cpp.so", 0x11F79EC), (void *) IsHostProfile, (void **) &_IsHostProfile);
 	MSHookFunction((void *) getAbsoluteAddress("libil2cpp.so", 0x175216C), (void *) InitTeamHeroList, (void **) &_InitTeamHeroList);
    hexPatches.aim = MemoryPatch::createWithHex(targetLibName,string2Offset(OBFUSCATE("0x1D70AF4")),"01 00 A0 E3 1E FF 2F E1");
    hexPatches.aim1 = MemoryPatch::createWithHex(targetLibName,string2Offset(OBFUSCATE("0x183D858 ")),"01 00 A0 E3 1E FF 2F E1");
    hexPatches.aim2 = MemoryPatch::createWithHex(targetLibName,string2Offset(OBFUSCATE(" 0x1AA3824 ")),"01 00 A0 E3 1E FF 2F E1");
    hexPatches.aim3 = MemoryPatch::createWithHex(targetLibName,string2Offset(OBFUSCATE(" 0xE98B44 ")),"01 00 A0 E3 1E FF 2F E1");
    hexPatches.hackhoichieu = MemoryPatch::createWithHex(targetLibName,string2Offset(OBFUSCATE("0x20F3338")),"01 00 A0 E3 1E FF 2F E1");
    LOGI(OBFUSCATE("Done"));
    #endif

    return NULL;
}

jobjectArray GetFeatureList(JNIEnv *env, jobject context) {
    jobjectArray ret;

    const char *features[] = {
        OBFUSCATE("Collapse_Chức Năng ESP"),
        OBFUSCATE("1_CollapseAdd_CheckBox_Kích Hoạt ESP"),
        OBFUSCATE("2_CollapseAdd_CheckBox_ESP Dòng Kẻ"),
        OBFUSCATE("3_CollapseAdd_CheckBox_ESP Hộp"),
        OBFUSCATE("6_CollapseAdd_CheckBox_ESP Máu"),
		OBFUSCATE("7_CollapseAdd_CheckBox_ESP Khoảng Cách"),
		OBFUSCATE("8_CollapseAdd_CheckBox_ESP 360°"),
		OBFUSCATE("5_CollapseAdd_CheckBox_ESP Đếm Địch"),
        OBFUSCATE("9_CollapseAdd_CheckBox_ESP Điểm Chấm"),
        OBFUSCATE("10_CollapseAdd_CheckBox_ESP Alert 360"),

        OBFUSCATE("Collapse_Chức Năng Khác Khác"),    OBFUSCATE("11_CollapseAdd_CheckBox_Bypass"),
        OBFUSCATE("12_CollapseAdd_CheckBox_Hack Map"),
        OBFUSCATE("13_CollapseAdd_SeekBar_<font color='blue'>Camera Fov </font>_1_15"),
        OBFUSCATE("14_CollapseAdd_CheckBox_Show Hp"),
        OBFUSCATE("15_CollapseAdd_CheckBox_Aim All Tướng"),
        OBFUSCATE("16_CollapseAdd_CheckBox_ShowSkillStateInfo"),
        OBFUSCATE("17_CollapseAdd_CheckBox_Fps120-IsPad"),
        OBFUSCATE("18_CollapseAdd_CheckBox_Show Lịch Sử"),
        OBFUSCATE("19_CollapseAdd_CheckBox_Show Name"),
        OBFUSCATE("20_CollapseAdd_CheckBox_Aim Elsu"),
        OBFUSCATE("21_CollapseAdd_CheckBox_Hủy Hồi Chiêu (Ảo)"),

        OBFUSCATE("Collapse_ESP SETTINGS"),
        OBFUSCATE("2000_CollapseAdd_SeekBar_Độ Dày_1_100"),
        OBFUSCATE("2001_CollapseAdd_SeekBar_Màu_0_19"),

        OBFUSCATE("Collapse_Liên Hệ Admin"),
        OBFUSCATE("2002_CollapseAdd_ButtonLink_TeleGram_https://t.me/Longbitcoin68"),
        //OBFUSCATE("2023_CollapseAdd_CheckBox_SĐT:0346201214"),
        //OBFUSCATE("2023_CollapseAdd_ButtonLink_Youtube_https://youtube.com/@hoangtai17"),
        //OBFUSCATE("-999_Button_log"),
    };
    int Total_Feature = (sizeof features / sizeof features[0]);
    ret = (jobjectArray)
            env->NewObjectArray(Total_Feature, env->FindClass(OBFUSCATE("java/lang/String")),
            env->NewStringUTF(""));

    for (int i = 0; i < Total_Feature; i++)
        env->SetObjectArrayElement(ret, i, env->NewStringUTF(features[i]));

    return (ret);
}

void Changes(JNIEnv *env, jclass clazz, jobject obj,
                                        jint featNum, jstring featName, jint value,
                                        jboolean boolean, jstring str) {
    switch (featNum) {
        case 1: 
            bEsp = boolean;
            
            break;
            
        case 2: 
            bEspLine = boolean;
            break;
            
        case 3: 
            bEspBox = boolean;
            break;
            
        case 4: 
            bEspHp = boolean;
                       break;  
        case 5: 
            bEspCountEnemy = boolean;
            break;
                    
        case 6: 
            bEspEnemyHealth = boolean;
            break;  
            
           case 9: 
            cccc = boolean;
            break;  
            
        case 7: 
            bEspEnemyDistance = boolean;
           break;
           
        case 8:
            bEspAlert = boolean;
            break;
            
            case 10:
            bAneko = boolean;
            break;
            
                     case 11:
feature1 = boolean;
                break;

        
        case 12:
            feature2 = boolean;
                        if (feature2) {
                hexPatches.hackmap1.Modify();
                hexPatches.hackmap2.Modify();
                hexPatches.hackmap3.Modify();
                
                


            } else {
                hexPatches.hackmap1.Restore();
                hexPatches.hackmap2.Restore();
                hexPatches.hackmap3.Restore();
                
                
                      
           //LOGI(OBFUSCATE("Off"));
            }

        break;
        
case 13:
        sliderValue = value;
          if (feature3) {
          hexPatches.camcao.Modify();
                      } else {
                   hexPatches.camcao.Restore();
        //LOGI(OBFUSCATE("Off"));
            }
        break;

case 14:
             feature4 = boolean;
           if (feature4) {
hexPatches.showteamhp1.Modify();
hexPatches.showteamhp2.Modify();
            } else {          
hexPatches.showteamhp1.Restore();
hexPatches.showteamhp2.Restore();
                //LOGI(OBFUSCATE("Off"));
            }
            break;

case 15:
            feature5 = boolean;
            if (feature5) {
  hexPatches.aimalltuong.Modify();
              } else {
  hexPatches.aimalltuong.Restore();
         
                //LOGI(OBFUSCATE("Off"));
            }
            break;

 case 16:
 feature6 = boolean;
if (feature6) {
  
    hexPatches.botro1.Modify();
    hexPatches.botro2.Modify(); 
    hexPatches.botro3.Modify();
    

              } else {
  
    hexPatches.botro1.Restore();
    hexPatches.botro2.Restore();
    hexPatches.botro3.Restore();

         
                //LOGI(OBFUSCATE("Off"));
            }
            break;
            case 17:
            feature7 = boolean;
if (feature7) {
  hexPatches.fps120.Modify();
  hexPatches.ispad.Modify();
  
              } else {
   hexPatches.fps120.Restore();
     hexPatches.ispad.Restore();

         
                //LOGI(OBFUSCATE("Off"));
            }
            break;
     case 18:
     feature8 = boolean;
     if (feature8) {       
                      hexPatches.showlichsu.Modify();
                      } else {
                   hexPatches.showlichsu.Restore();
        //LOGI(OBFUSCATE("Off"));
            }
        break;
        
        case 19:
        feature9 = boolean;
        if (feature9) {
        hexPatches.showname.Modify();
        } else {
        hexPatches.showname.Modify();
                //LOGI(OBFUSCATE("Off"));
            }
        break;

case 20:
        feature10 = boolean;
        if (feature10) {
        hexPatches.aim.Modify();
          hexPatches.aim1.Modify();
                  hexPatches.aim2.Modify();
          hexPatches.aim3.Modify();
                        } else {
        
        hexPatches.aim.Restore();
          hexPatches.aim1.Restore();
                  hexPatches.aim2.Restore();
          hexPatches.aim3.Restore();
        //LOGI(OBFUSCATE("Off"));
            }
        break;


                    
        case 2000:
            SizeLine = value;
        break;
        
        case 2001:
        if (value == 0) {
        EspColor = Color::White(); // Cyan
        } else if (value == 1) {
        EspColor = Color::Cyan(); // Azul Claro
        } else if (value == 2) {
        EspColor = Color(26, 163, 255, 255); // Azul
        } else if (value == 3) {
        EspColor = Color(0, 255, 153, 255); // Verde piscina
        } else if (value == 4) {
        EspColor = Color(0, 255, 0, 255); // Verde
        } else if (value == 5) {
        EspColor = Color(255, 0, 255, 255); // Rosa
        } else if (value == 6) {
        EspColor = Color(255, 0, 102, 255); // Rosa Escuro
        } else if (value == 7) {
        EspColor = Color(204, 0, 153, 255); // Roxo
        } else if (value == 8) {
        EspColor = Color(255, 255, 0, 255); // Amarelo
        } else if (value == 9) {
        EspColor = Color(255, 204, 0, 255); // Dourado
        } else if (value == 10) {
         EspColor = Color(102, 102, 153, 255); // Mix
        } else if (value == 11) {
         EspColor = Color(153, 102, 140, 255); // Mix 2
        } else if (value == 12) {
         EspColor = Color(153, 102, 102, 255); // Mix 3
        } else if (value == 13) {
         EspColor = Color(255, 255, 255, 255); // Branco
        } else if (value == 14) {
         EspColor = Color(204, 102, 0, 255); // Laranja
        } else if (value == 15) {
         EspColor = Color(153, 102, 102, 255); // Vinho
        } else if (value == 16) {
         EspColor = Color::Red(); // Red
        } else if (value == 17) {
         EspColor = Color::Black(); // Red
        } else if (value == 18) {
         EspColor = Color::Green(); // Red
        } else if (value == 19) {
         EspColor = Color::Blue(); // Red
        }
        break;

    }
}

void *doisanh(void* obj, ESPManager* list){
    if (!list->enemies->empty()) {
        int get_idObj = LActorRoot_get_ObjID(obj);
        for (int i = 0; i < list->enemies->size(); i++) {
            void *mEnemy = (*list->enemies)[i]->object;
            int get_idEnemy = ActorLinker_get_ObjID(mEnemy);
            if (get_idObj == get_idEnemy) return mEnemy;
        }
    }
    return NULL;
}

bool check_min(int dis){
    bool check = true;
    for (int i = 0; i < espManager->enemies->size(); i++) {
       void *Enemy = (*espManager->enemies)[i]->object;
       void *MyPlayer = espManager->MyPlayer;
       Vector3 MyPlayerPos = ActorLinker_getPosition(MyPlayer);
       Vector3 EnemyPos = VInt2Vector(LActorRoot_get_location(Enemy), LActorRoot_get_forward(Enemy));
       Vector3 MyPlayerScreenPos = Camera_WorldToScreenPoint(Camera_get_main(NULL), MyPlayerPos);
       Vector3 EnemyScreenPos = Camera_WorldToScreenPoint(Camera_get_main(NULL), EnemyPos);
       int Distance = (int) Vector3::Distance(MyPlayerScreenPos, EnemyScreenPos) / 30;
       if (dis>Distance) check = false;
    }
    if (check) {return true;} else {return false;}
    return false;
}

int countEnemy = 0;
void OnDrawEsp(JNIEnv * env, jclass type, jobject espView, jobject canvas) {
    ESP espOverlay = ESP(env, espView, canvas);
    if (espOverlay.isValid()) {
       DrawESP(espOverlay, espOverlay.getWidth(), espOverlay.getHeight());
       int screenWidth = espOverlay.getWidth();
       int screenHeight = espOverlay.getHeight();
       ESP esp = espOverlay;
       // esp.DrawText(Color::Yellow(), "LongBTC", Vector2(screenWidth / 9, screenHeight/15), 20);
       Vector2 screen(screenWidth, screenHeight);
    if (espManager->enemies->empty() || !bEsp) {
        check_move = false;
        return;
    }
    
    countEnemy = 0;
    int distance_near =99999;
    for (int i = 0; i < espManager->enemies->size(); i++) {
        void *Enemy = (*espManager->enemies)[i]->object;
        void *MyPlayer = espManager->MyPlayer;
     //   void *Cam = Camera_get_main();
        float mScale = screenHeight / (float)965;
        if (Enemy!=NULL && MyPlayer!=NULL) {
            void *Enemy_ActorLinker = doisanh(Enemy, ActorLinker_enemy);
            void *LObjWrapper = *(void**)((uint64_t)Enemy + 0x1FC); 
            void *ValuePropertyComponent = *(void**)((uint64_t)Enemy + 0x210); 
            if (LObjWrapper!=NULL&&ValuePropertyComponent!=NULL){
                if (!LObjWrapper_get_IsDeadState(LObjWrapper)){
                    countEnemy++;
                    Vector3 MyPlayerPos = ActorLinker_getPosition(MyPlayer);
                    Vector3 EnemyPos = VInt2Vector(LActorRoot_get_location(Enemy), LActorRoot_get_forward(Enemy));
                    if (ActorLinker_get_bVisible(Enemy_ActorLinker)) EnemyPos = ActorLinker_getPosition(Enemy_ActorLinker);
                    Vector3 MyPlayerScreenPos = Camera_WorldToScreenPoint(Camera_get_main(NULL), MyPlayerPos);
                    Vector3 EnemyScreenPos = Camera_WorldToScreenPoint(Camera_get_main(NULL), EnemyPos);
                    Vector3 PosNew = {0.f, 0.f, 0.f};
                    PosNew = Camera_WorldToScreenPoint(Camera_get_main(NULL), EnemyPos);
                    Vector2 DrawFrom = Vector2(screenWidth / 2, screenHeight / 11);
                    Vector2 DrawTo = Vector2((screenWidth- (screenWidth - PosNew.X)) + -90, (screenHeight - PosNew.Y - -20.0f));
                    int EnemyHp = ValuePropertyComponent_get_actorHp(ValuePropertyComponent);     
                        
	
                    if (bEspEnemyDistance) {
                        char dis_str[0xFF] = {0};
                        int Distance = (int) Vector3::Distance(MyPlayerPos, EnemyScreenPos) / 30;
                        if (Distance < 0) {
                            Distance = (Distance * -1) / 30;
                            } else {
                            distance_near = Distance;
                            sprintf(dis_str, "[ %d M ]", Distance);
                            esp.DrawText(EspColor, dis_str, Vector2(DrawTo.X, DrawTo.Y + 40.0f), 17);
                        }
                    }
                    
                    if (cccc) {
                    esp.DrawText(EspColor, "⬤", Vector2(DrawFrom.X, DrawFrom.Y), 16.0f);
                    esp.DrawText(EspColor, "⬤",  Vector2(DrawTo.X, DrawTo.Y), 16.0f);
                    }
                    if (bEspLine) esp.DrawLine(EspColor, SizeLine, DrawFrom, DrawTo);
                    if (bEspEnemyHealth){
                        bool m_move = *(bool*)((uint64_t)MyPlayer + 0x2BE); 
                        if (m_move) check_move = true;
                        int EnemyHpTotal = 0;
                        if (check_move) {
                            EnemyHpTotal = ValuePropertyComponent_get_actorHpTotal(ValuePropertyComponent);
                            } else {
                                char loading_st[0xFF] = {0};
                                string s = "Đợi 1s Để Load..";
                                strcpy(loading_st, s.c_str());
                                esp.DrawText(EspColor, loading_st, Vector2(screenWidth /2, screenHeight /5), 25);
                            }
                        Vector2 p1 = Vector2(DrawTo.X - (BoxWidth/2) - 16, DrawTo.Y + 0);
                        esp.DrawH(p1,EnemyHp,EnemyHpTotal, 160, 15, 7);
                        
                        //do_draw_health(esp, Vector2(DrawTo.X - 50.0f, DrawTo.Y + 15.0f), EnemyHp, EnemyHpTotal);
				    }
                    if (bEspBox){
                        Vector2 v1 = Vector2(DrawTo.X - (BoxWidth/2), DrawTo.Y);
                        Vector2 v2 = Vector2(DrawTo.X + (BoxWidth/2), DrawTo.Y);
                        Vector2 v3 = Vector2(DrawTo.X + (BoxWidth/2), DrawTo.Y - BoxHeight);
                        Vector2 v4 = Vector2(DrawTo.X - (BoxWidth/2), DrawTo.Y - BoxHeight);
                        esp.DrawLine(EspColor, SizeLine, v1, v2);
                        esp.DrawLine(EspColor, SizeLine, v2, v3);
                        esp.DrawLine(EspColor, SizeLine, v3, v4);
                        esp.DrawLine(EspColor, SizeLine, v4, v1);
                    }
                    
                    if(bAneko && isOutsideSafeZone(DrawTo, screen)){
	            char dis_str[0xFF] = { 0 };
				int Distance = (int) Vector3::Distance(MyPlayerPos, EnemyScreenPos) / 30;
                     if (Distance != NULL)                        
                        sprintf(dis_str, "[ %.2d M]", Distance);
                        Vector2 hintDotRenderPos = pushToScreenBorder(DrawTo, screen, (int)((mScale * 30) / 35));
                        Vector2 hintTextRenderPos = pushToScreenBorder(DrawTo, screen, -(int)((mScale * 4)));
                        esp.DrawFilledCircle(Color::Green(), hintDotRenderPos,(mScale * 50));
                        esp.DrawText(Color(0, 0, 255, 255), dis_str, hintTextRenderPos, 16);
					}
					
                    
                    
                 }  
             }
        } 
    }
   /* if (bEspCountEnemy){
        char countEnemy_str[0xFF] = {0};     
        s.append(std::to_string((int)countEnemy));     
        strcpy(countEnemy_str, s.c_str());
        esp.DrawFilledCircle(Color::White(), Vector2(screenWidth / 2, 105),screenHeight / 11);
        esp.DrawText(Color::Red(), countEnemy_str, Vector2(screenWidth /2, screenHeight /11), 50);
        
    }*/
	
	
	if (bEspCountEnemy){
        char countEnemy_str[0xFF] = {0};
        string s = "";
        s.append(std::to_string((int)countEnemy));
      //  s.append("/");
      //  s.append(std::to_string((int)espManager->enemies->size()));
        strcpy(countEnemy_str, s.c_str());
		esp.DrawFilledCircle(Color::White(), Vector2(screenWidth / 2, screenHeight / 11), 30);
        esp.DrawText(Color::Black(), countEnemy_str, Vector2(screenWidth /2, screenHeight /10), 30);
    }
	
	
	
    
    }
}

__attribute__((constructor))
void lib_main() {
    pthread_t ptid;
    pthread_create(&ptid, NULL, hack_thread, NULL);
}

int RegisterMenu(JNIEnv *env) {
    JNINativeMethod methods[] = {
        {OBFUSCATE("Icon"), OBFUSCATE("()Ljava/lang/String;"), reinterpret_cast<void *>(Icon)},
        {OBFUSCATE("IconWebViewData"),  OBFUSCATE("()Ljava/lang/String;"), reinterpret_cast<void *>(IconWebViewData)},
        {OBFUSCATE("IsGameLibLoaded"),  OBFUSCATE("()Z"), reinterpret_cast<void *>(isGameLibLoaded)},
        {OBFUSCATE("Init"),  OBFUSCATE("(Landroid/content/Context;Landroid/widget/TextView;Landroid/widget/TextView;)V"), reinterpret_cast<void *>(Init)},
        {OBFUSCATE("SettingsList"),  OBFUSCATE("()[Ljava/lang/String;"), reinterpret_cast<void *>(SettingsList)},
        {OBFUSCATE("GetFeatureList"),  OBFUSCATE("()[Ljava/lang/String;"), reinterpret_cast<void *>(GetFeatureList)},
		{OBFUSCATE("DrawEsp"), OBFUSCATE("(Lcom/android/support/esp/EspView;Landroid/graphics/Canvas;)V"), reinterpret_cast<void *>(OnDrawEsp)},
    };

    jclass clazz = env->FindClass(OBFUSCATE("com/android/support/Menu"));
    if (!clazz)
        return JNI_ERR;
    if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) != 0)
        return JNI_ERR;
    return JNI_OK;
}

int RegisterPreferences(JNIEnv *env) {
    JNINativeMethod methods[] = {
            {OBFUSCATE("Changes"), OBFUSCATE("(Landroid/content/Context;ILjava/lang/String;IZLjava/lang/String;)V"), reinterpret_cast<void *>(Changes)},
    };
    jclass clazz = env->FindClass(OBFUSCATE("com/android/support/Preferences"));
    if (!clazz)
        return JNI_ERR;
    if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) != 0)
        return JNI_ERR;
    return JNI_OK;
}

int RegisterMain(JNIEnv *env) {
    JNINativeMethod methods[] = {
            {OBFUSCATE("CheckOverlayPermission"), OBFUSCATE("(Landroid/content/Context;)V"), reinterpret_cast<void *>(CheckOverlayPermission)},
    };
    jclass clazz = env->FindClass(OBFUSCATE("com/android/support/Main"));
    if (!clazz)
        return JNI_ERR;
    if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) != 0)
        return JNI_ERR;

    return JNI_OK;
}

extern "C"
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_6);
    if (RegisterMenu(env) != 0)
        return JNI_ERR;
    if (RegisterPreferences(env) != 0)
        return JNI_ERR;
    if (RegisterMain(env) != 0)
        return JNI_ERR;
    return JNI_VERSION_1_6;
}
