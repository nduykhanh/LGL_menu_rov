#ifndef ESP_H
#define ESP_H


#include <jni.h>


class ESP {
private:
    JNIEnv *_env;
    jobject _cvsView;
    jobject _cvs;

public:
    ESP() {
        _env = nullptr;
        _cvsView = nullptr;
        _cvs = nullptr;
    }

    ESP(JNIEnv *env, jobject cvsView, jobject cvs) {
        this->_env = env;
        this->_cvsView = cvsView;
        this->_cvs = cvs;
    }

    bool isValid() const {
        return (_env != nullptr && _cvsView != nullptr && _cvs != nullptr);
    }

    int getWidth() const {
        if (isValid()) {
            jclass canvas = _env->GetObjectClass(_cvs);
            jmethodID width = _env->GetMethodID(canvas, "getWidth", "()I");
            return _env->CallIntMethod(_cvs, width);
        }
        return 0;
    }

    int getHeight() const {
        if (isValid()) {
            jclass canvas = _env->GetObjectClass(_cvs);
            jmethodID width = _env->GetMethodID(canvas, "getHeight", "()I");
            return _env->CallIntMethod(_cvs, width);
        }
        return 0;
    }

    void
    DrawLine(Color color, float thickness, Vector2 start, Vector2 end) {
        if (isValid()) {
            jclass canvasView = _env->GetObjectClass(_cvsView);
            jmethodID drawline = _env->GetMethodID(canvasView, "DrawLine",
                                                   "(Landroid/graphics/Canvas;IIIIFFFFF)V");
            _env->CallVoidMethod(_cvsView, drawline, _cvs, (int) color.A, (int) color.R,
                                 (int) color.G, (int) color.B,
                                 thickness,
                                 start.X, start.Y, end.X, end.Y);
        }
    }

    void DrawText(Color color, const char *txt, Vector2 pos, float size) {
        if (isValid()) {
            jclass canvasView = _env->GetObjectClass(_cvsView);
            jmethodID drawtext = _env->GetMethodID(canvasView, "DrawText",
                                                   "(Landroid/graphics/Canvas;IIIILjava/lang/String;FFF)V");
            _env->CallVoidMethod(_cvsView, drawtext, _cvs, (int) color.A, (int) color.R,
                                 (int) color.G, (int) color.B,
                                 _env->NewStringUTF(txt), pos.X, pos.Y, size);
        }
    }

    void DrawCircle(Color color,float stroke, Vector2 pos, float radius) {
        if (isValid()) {
            jclass canvasView = _env->GetObjectClass(_cvsView);
            jmethodID drawcircle = _env->GetMethodID(canvasView, "DrawCircle",
                                                     "(Landroid/graphics/Canvas;IIIIFFFF)V");
            _env->CallVoidMethod(_cvsView, drawcircle, _cvs, (int) color.A, (int) color.R,
                                 (int) color.G, (int) color.B,stroke, pos.X, pos.Y, radius);
        }
    }
    
    void DrawBox(Color color, float stroke, Rect rect) {
        Vector2 v1 = Vector2(rect.m_XMin, rect.m_YMin);
        Vector2 v2 = Vector2(rect.m_XMin + rect.m_Width, rect.m_YMin);
        Vector2 v3 = Vector2(rect.m_XMin + rect.m_Width, rect.m_YMin + rect.m_Height);
        Vector2 v4 = Vector2(rect.m_XMin, rect.m_YMin + rect.m_Height);

        DrawLine(color, stroke, v1, v2); // ALINHAR
        DrawLine(color, stroke, v2, v3); // LINHA DIREITA
        DrawLine(color, stroke, v3, v4); // LINHA ABAIXO
        DrawLine(color, stroke, v4, v1); // LINHA ESQUERDA
    }


    void DrawHorizontalHealthBar(Vector2 screenPos, float width, float maxHealth, float currentHealth) {
        screenPos -= Vector2(0.0f, 8.0f);
        DrawBox(Color(0, 0, 0, 255), 3, Rect(screenPos.X, screenPos.Y, width + 2, 5.0f));
        screenPos += Vector2(1.0f, 1.0f);
        Color clr = Color(0, 255, 0, 255);
        float hpWidth = (currentHealth * width) / maxHealth;
        if (currentHealth <= (maxHealth * 0.6)) {
            clr = Color(255, 255, 0, 255);
        }
        if (currentHealth < (maxHealth * 0.3)) {
            clr = Color(255, 0, 0, 255);
        }
        DrawBox(clr, 3, Rect(screenPos.X, screenPos.Y, hpWidth, 3.0f));
    }

    void DrawCrosshair(Color clr, Vector2 center, float size, float thickness) {
        float x = center.X - (size / 2.0f);
        float y = center.Y - (size / 2.0f);
        DrawLine(clr, thickness, Vector2(x, center.Y), Vector2(x + size, center.Y));
        DrawLine(clr, thickness, Vector2(center.X, y), Vector2(center.X, y + size));
    }
    
    void DrawB(Vector2 pos, float height,float widght) {
        Vector2 v1 = Vector2(pos.X, pos.Y-height/2-1);
        Vector2 v2 = Vector2(pos.X + widght, pos.Y-height/2-1);
        Color clr2 = Color(255, 0, 0, 80);
        DrawLine(clr2, height, v1, v2);
    }
    
    void DrawFilledCircle(Color color, Vector2 pos, float radius) {
        if (isValid()) {
            jclass canvasView = _env->GetObjectClass(_cvsView);
            jmethodID drawfilledcircle = _env->GetMethodID(canvasView, "DrawFilledCircle",
                                                           "(Landroid/graphics/Canvas;IIIIFFF)V");
            _env->CallVoidMethod(_cvsView, drawfilledcircle, _cvs, (int) color.A, (int) color.R,
                                 (int) color.G, (int) color.B, pos.X, pos.Y, radius);
        }
    }
	
	void DrawFilledCircle2(Color color, Vector3 pos, float radius) {
        if (isValid()) {
            jclass canvasView = _env->GetObjectClass(_cvsView);
            jmethodID drawfilledcircle = _env->GetMethodID(canvasView, "DrawFilledCircle",
                                                           "(Landroid/graphics/Canvas;IIIIFFF)V");
            _env->CallVoidMethod(_cvsView, drawfilledcircle, _cvs, (int) color.A, (int) color.R,
                                 (int) color.G, (int) color.B, pos.X, pos.Y, radius);
        }
    }
    
	void DrawText2(Color color, float stroke, std::string str, Vector3 pos, float size) {
        
		if (isValid()) {
            jclass canvasView = _env->GetObjectClass(_cvsView);
            jmethodID drawtext = _env->GetMethodID(canvasView, "DrawText",
                                                   "(Landroid/graphics/Canvas;IIIIFLjava/lang/String;FFF)V");
            _env->CallVoidMethod(_cvsView, drawtext, _cvs, (int) color.A, (int) color.R,
                                 (int) color.G, (int) color.B,stroke,
                                 _env->NewStringUTF(str.c_str()), pos.X, pos.Y, size);
        }
    }

	
    
    void DrawH(Vector2 pos,float curHp, float maxHp, float height,float widght, float stroke) {
        Vector2 v1 = Vector2(pos.X + widght/2 + stroke-1, pos.Y - stroke+1);
        Vector2 v2 = Vector2(pos.X + widght/2 + stroke-1, pos.Y - height - stroke+1);
        
        Color clr1 = Color(0, 0, 0, 255);
        DrawLine(clr1, widght + stroke, v1, v2);
        
        float args0 = (curHp * height) / maxHp;
        Vector2 v3 = Vector2(pos.X + widght/2 + stroke-1, pos.Y - stroke-1);
        Vector2 v4 = Vector2(pos.X + widght/2 + stroke-1, pos.Y - args0);
        Color clr2 = Color(0, 255, 0, 255);
        if (curHp <= (maxHp * 0.6)) clr2 = Color(255, 255, 0, 255);
        if (curHp < (maxHp * 0.3)) clr2 = Color(255, 0, 0, 255);
        
        char hp_str[0xFF] = {0};
        string s;
        s.append(std::to_string((int)(curHp/maxHp*100)).c_str());
        s.append("%");
        strcpy(hp_str, s.c_str());
        
        DrawText(Color::White(), hp_str, v4 - Vector2(height/10+14,-(height/10+0)), height/10+2);
        DrawLine(clr2, widght, v3, v4);

}
    
};

#endif
