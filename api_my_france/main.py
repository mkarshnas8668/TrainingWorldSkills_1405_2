from fastapi import FastAPI, HTTPException, Header, Depends
from fastapi.responses import JSONResponse, FileResponse, HTMLResponse
from fastapi.staticfiles import StaticFiles
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, EmailStr, Field
from datetime import datetime
from typing import Optional, List, Dict, Any
import uuid
import os
import json
from pathlib import Path

app = FastAPI(title="My France API - WorldSkills 2024")

# ============================================================
# CORS Middleware (برای دسترسی از اندروید)
# ============================================================
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ============================================================
# مانت کردن پوشه استاتیک
# ============================================================
app.mount("/sources", StaticFiles(directory="static/sources"), name="sources")

# ============================================================
# مدل‌های داده (Data Models)
# ============================================================

class SignInRequest(BaseModel):
    userEmailAddress: EmailStr
    userPassword: str = Field(..., min_length=6)

class SignInResponse(BaseModel):
    msg: str
    data: Dict[str, str]

# ============================================================
# دیتابیس موقت در حافظه (In-Memory Database)
# ============================================================

# دیتابیس کاربران
users_db = {
    "player@example.com": {
        "password": "123abc",
        "auth_token": None,
        "favorites": []
    },
    "sarah@example.com": {
        "password": "sarah123",
        "auth_token": None,
        "favorites": []
    },
    "michael@example.com": {
        "password": "mike456",
        "auth_token": None,
        "favorites": []
    }
}

# دیتابیس توکن‌های فعال
active_tokens = {}

# ============================================================
# دیتابیس سفرنامه‌ها (Diaries) - با استفاده از تصاویر موجود
# ============================================================

diaries_db = {
    # ===== EIFFEL TOWER DIARIES (با تصاویر scene_1, scene_2, scene_3) =====
    "BA23617D-42DA-8C4A-F569-C1915B985B1": {
        "title": "A Magical Day at the Eiffel Tower",
        "publisher_username": "emma_traveler",
        "thumbnail": "sources/Eiffel/scene_1.jpg",
        "main_text": json.dumps([
            {"type": "text", "content": "The Eiffel Tower was absolutely breathtaking at sunset. The golden lights made the entire structure glow like a dream."},
            {"type": "image", "src": "sources/Eiffel/scene_1.jpg"},
            {"type": "text", "content": "We arrived around 5 PM and waited in line for about 30 minutes. The elevator ride to the top was smooth and offered incredible views of Paris."},
            {"type": "image", "src": "sources/Eiffel/scene_2.jpg"},
            {"type": "text", "content": "At the top, you can see the entire city stretching out before you. The Seine River, Notre-Dame, and the Arc de Triomphe are all visible."},
            {"type": "image", "src": "sources/Eiffel/scene_3.jpg"},
            {"type": "text", "content": "I highly recommend visiting during golden hour. The photos you'll get are unforgettable!"}
        ]),
        "publish_datetime": "2024-08-24T12:05:00",
        "images": ["sources/Eiffel/scene_1.jpg", "sources/Eiffel/scene_2.jpg", "sources/Eiffel/scene_3.jpg"]
    },
    
    # ===== EIFFEL TOWER DIARY 2 =====
    "EA23618D-53EB-9D5B-G678-H3039GG33E5": {
        "title": "Eiffel Tower at Night",
        "publisher_username": "night_owl_paris",
        "thumbnail": "sources/Eiffel/scene_2.jpg",
        "main_text": json.dumps([
            {"type": "text", "content": "Visiting the Eiffel Tower at night is a completely different experience. The tower sparkles every hour on the hour."},
            {"type": "image", "src": "sources/Eiffel/scene_2.jpg"},
            {"type": "text", "content": "The Champ de Mars park below is filled with people having picnics and enjoying the view."},
            {"type": "image", "src": "sources/Eiffel/scene_3.jpg"},
            {"type": "text", "content": "We climbed the stairs to the second floor - it's worth the effort!"}
        ]),
        "publish_datetime": "2024-08-22T21:30:00",
        "images": ["sources/Eiffel/scene_2.jpg", "sources/Eiffel/scene_3.jpg"]
    },
    
    # ===== LOUVRE MUSEUM DIARIES (با تصاویر scene_4, scene_5, scene_6) =====
    "17B9C94E-F829-6088-FA92-9A07EEA00A8E": {
        "title": "Exploring the Louvre Museum",
        "publisher_username": "art_lover_france",
        "thumbnail": "sources/Louvre/scene_4.jpg",
        "main_text": json.dumps([
            {"type": "text", "content": "The Louvre is massive! You could spend days here and still not see everything."},
            {"type": "image", "src": "sources/Louvre/scene_4.jpg"},
            {"type": "text", "content": "The Mona Lisa is smaller than I expected, but still magical to see in person."},
            {"type": "image", "src": "sources/Louvre/scene_5.jpg"},
            {"type": "text", "content": "The Winged Victory of Samothrace is incredibly dramatic - don't miss it!"},
            {"type": "image", "src": "sources/Louvre/scene_6.jpg"},
            {"type": "text", "content": "The glass pyramid entrance is a beautiful contrast to the historic architecture."}
        ]),
        "publish_datetime": "2024-08-20T14:30:00",
        "images": ["sources/Louvre/scene_4.jpg", "sources/Louvre/scene_5.jpg", "sources/Louvre/scene_6.jpg"]
    },
    
    # ===== LOUVRE DIARY 2 =====
    "28C0D05F-6G93-1H9F-K123-M7474LL66H8": {
        "title": "Hidden Gems of the Louvre",
        "publisher_username": "museum_enthusiast",
        "thumbnail": "sources/Louvre/scene_5.jpg",
        "main_text": json.dumps([
            {"type": "text", "content": "Beyond the Mona Lisa, the Louvre has incredible works that are less crowded."},
            {"type": "image", "src": "sources/Louvre/scene_5.jpg"},
            {"type": "text", "content": "The French paintings section is amazing, especially the large canvases by Delacroix."},
            {"type": "image", "src": "sources/Louvre/scene_6.jpg"},
            {"type": "text", "content": "The Egyptian antiquities department is fascinating and not as busy."}
        ]),
        "publish_datetime": "2024-08-19T11:15:00",
        "images": ["sources/Louvre/scene_5.jpg", "sources/Louvre/scene_6.jpg"]
    },
    
    # ===== NOTRE-DAME CATHEDRAL DIARIES (با تصاویر scene_7, scene_8) =====
    "3C8F27A1-53EB-7D5B-G678-H2928FFB11C2": {
        "title": "Notre-Dame Cathedral Resilience",
        "publisher_username": "history_buff",
        "thumbnail": "sources/NotreDame/scene_7.jpg",
        "main_text": json.dumps([
            {"type": "text", "content": "Even under reconstruction, Notre-Dame inspires awe. The craftsmanship of the medieval builders is remarkable."},
            {"type": "image", "src": "sources/NotreDame/scene_7.jpg"},
            {"type": "text", "content": "The nearby area has many small cafes where you can sit and sketch the cathedral."},
            {"type": "image", "src": "sources/NotreDame/scene_8.jpg"},
            {"type": "text", "content": "The restoration work is progressing beautifully. Can't wait to see it completed!"}
        ]),
        "publish_datetime": "2024-08-18T09:15:00",
        "images": ["sources/NotreDame/scene_7.jpg", "sources/NotreDame/scene_8.jpg"]
    },
    
    # ===== NOTRE-DAME DIARY 2 =====
    "39D9F38B2-74FD-8E6C-H789-J4140GG33D4": {
        "title": "Walking Around Île de la Cité",
        "publisher_username": "paris_walker",
        "thumbnail": "sources/NotreDame/scene_8.jpg",
        "main_text": json.dumps([
            {"type": "text", "content": "The area around Notre-Dame is charming, especially along the Seine riverbanks."},
            {"type": "image", "src": "sources/NotreDame/scene_8.jpg"},
            {"type": "text", "content": "The view of the cathedral from the Pont de l'Archevêché is perfect for photos."},
            {"type": "image", "src": "sources/NotreDame/scene_7.jpg"},
            {"type": "text", "content": "Don't miss the Sainte-Chapelle nearby - its stained glass windows are incredible!"}
        ]),
        "publish_datetime": "2024-08-16T16:45:00",
        "images": ["sources/NotreDame/scene_8.jpg", "sources/NotreDame/scene_7.jpg"]
    },
    
    # ===== ADDITIONAL DIARIES با استفاده از تصاویر موجود =====
    "4D9F38B2-63EC-8E6C-H789-I3039GG22D3": {
        "title": "Sunset Cruise on the Seine River",
        "publisher_username": "river_lover",
        "thumbnail": "sources/Eiffel/scene_2.jpg",
        "main_text": json.dumps([
            {"type": "text", "content": "Taking a sunset cruise on the Seine River is an absolute must when visiting Paris!"},
            {"type": "image", "src": "sources/Eiffel/scene_2.jpg"},
            {"type": "text", "content": "We passed under beautiful bridges and saw many landmarks lit up at night."},
            {"type": "image", "src": "sources/Eiffel/scene_3.jpg"},
            {"type": "text", "content": "The Eiffel Tower sparkling at the top of each hour was the highlight of the evening."}
        ]),
        "publish_datetime": "2024-08-15T18:45:00",
        "images": ["sources/Eiffel/scene_2.jpg", "sources/Eiffel/scene_3.jpg"]
    },
    
    "5E0G49C3-74FD-9F7D-I890-J4140HH33E4": {
        "title": "A Day at Montmartre",
        "publisher_username": "paris_explorer",
        "thumbnail": "sources/Louvre/scene_5.jpg",
        "main_text": json.dumps([
            {"type": "text", "content": "Montmartre is full of charm with its cobblestone streets and artistic atmosphere."},
            {"type": "image", "src": "sources/Louvre/scene_5.jpg"},
            {"type": "text", "content": "The Sacré-Cœur Basilica offers the best panoramic view of Paris."},
            {"type": "image", "src": "sources/Louvre/scene_6.jpg"},
            {"type": "text", "content": "Place du Tertre is filled with portrait artists painting in the square."}
        ]),
        "publish_datetime": "2024-08-12T11:20:00",
        "images": ["sources/Louvre/scene_5.jpg", "sources/Louvre/scene_6.jpg"]
    },
    
    "6F1H50D4-85GE-0G8E-J901-K5251II44F5": {
        "title": "Versailles Palace Tour",
        "publisher_username": "royal_enthusiast",
        "thumbnail": "sources/NotreDame/scene_8.jpg",
        "main_text": json.dumps([
            {"type": "text", "content": "The Palace of Versailles is absolutely magnificent! The Hall of Mirrors is breathtaking."},
            {"type": "image", "src": "sources/NotreDame/scene_8.jpg"},
            {"type": "text", "content": "The gardens are enormous and perfectly manicured. You could spend all day exploring them."},
            {"type": "text", "content": "Marie Antoinette's estate is a charming escape from the main palace crowds."}
        ]),
        "publish_datetime": "2024-08-10T09:30:00",
        "images": ["sources/NotreDame/scene_8.jpg"]
    },
    
    "7G2I61E5-96HF-1H9F-K012-L6362JJ55G6": {
        "title": "French Culinary Experience",
        "publisher_username": "foodie_france",
        "thumbnail": "sources/Eiffel/scene_1.jpg",
        "main_text": json.dumps([
            {"type": "text", "content": "The food in France is absolutely incredible! Croissants for breakfast are a daily ritual."},
            {"type": "image", "src": "sources/Eiffel/scene_1.jpg"},
            {"type": "text", "content": "We took a cooking class and learned to make authentic French macarons."},
            {"type": "image", "src": "sources/Eiffel/scene_2.jpg"},
            {"type": "text", "content": "Don't miss trying escargot and duck confit - they're delicious!"}
        ]),
        "publish_datetime": "2024-08-07T13:15:00",
        "images": ["sources/Eiffel/scene_1.jpg", "sources/Eiffel/scene_2.jpg"]
    }
}

# ============================================================
# دیتابیس جاذبه‌های گردشگری (با استفاده از نقشه‌های موجود)
# ============================================================

attractions_db = [
    {
        "id": 1,
        "name": "Eiffel Tower",
        "introduction": "The Eiffel Tower is a wrought-iron lattice tower on the Champ de Mars in Paris, France. It is named after the engineer Gustave Eiffel, whose company designed and built the tower. Constructed from 1887 to 1889 as the centerpiece of the 1889 World's Fair, it has become a global cultural icon of France and one of the most recognizable structures in the world.",
        "image": "sources/Eiffel/scene_1.jpg",
        "location_map": "sources/Maps/map_1.jpg",
        "latitude": 48.8584,
        "longitude": 2.2945
    },
    {
        "id": 2,
        "name": "Louvre Museum",
        "introduction": "The Louvre, or the Louvre Museum, is the world's largest art museum and a historic monument in Paris, France. A central landmark of the city, it is located on the Right Bank of the Seine in the city's 1st arrondissement. Approximately 38,000 objects from prehistory to the 21st century are exhibited over an area of 72,735 square meters.",
        "image": "sources/Louvre/scene_4.jpg",
        "location_map": "sources/Maps/map_5.jpg",
        "latitude": 48.8606,
        "longitude": 2.3376
    },
    {
        "id": 3,
        "name": "Notre-Dame Cathedral",
        "introduction": "Notre-Dame de Paris, referred to simply as Notre-Dame, is a medieval Catholic cathedral on the Île de la Cité in the 4th arrondissement of Paris. The cathedral was consecrated to the Virgin Mary and is considered one of the finest examples of French Gothic architecture.",
        "image": "sources/NotreDame/scene_7.jpg",
        "location_map": "sources/Maps/map_9.jpg",
        "latitude": 48.8530,
        "longitude": 2.3499
    },
    {
        "id": 4,
        "name": "Arc de Triomphe",
        "introduction": "The Arc de Triomphe de l'Étoile is one of the most famous monuments in Paris, France, standing at the western end of the Champs-Élysées at the center of Place Charles de Gaulle. It honors those who fought and died for France in the French Revolutionary and Napoleonic Wars.",
        "image": "sources/Attractions/scene_9.jpg",
        "location_map": "sources/Maps/map_3.jpg",
        "latitude": 48.8738,
        "longitude": 2.2950
    },
    {
        "id": 5,
        "name": "Sacré-Cœur Basilica",
        "introduction": "The Basilica of the Sacred Heart of Paris, commonly known as Sacré-Cœur Basilica, is a Roman Catholic church and minor basilica dedicated to the Sacred Heart of Jesus. It is located at the summit of the butte Montmartre, the highest point in the city.",
        "image": "sources/Attractions/scene_10.jpg",
        "location_map": "sources/Maps/map_7.jpg",
        "latitude": 48.8867,
        "longitude": 2.3431
    },
    {
        "id": 6,
        "name": "Seine River Cruise",
        "introduction": "The Seine River is a 777-kilometer-long river in northern France. Its drainage basin is in the Paris Basin. It rises at Source-Seine, in the Langres plateau, flowing through Paris and into the English Channel at Le Havre.",
        "image": "sources/Eiffel/scene_2.jpg",
        "location_map": "sources/Maps/map_6.jpg",
        "latitude": 48.8566,
        "longitude": 2.3522
    }
]

# ============================================================
# توابع کمکی
# ============================================================

def generate_auth_token() -> str:
    """تولید توکن تصادفی ۲۵ کاراکتری"""
    return uuid.uuid4().hex.upper()[:25]

def format_datetime(dt_str: str) -> str:
    """تبدیل تاریخ به فرمت 'Aug 24, 2024 12:05 AM'"""
    try:
        dt = datetime.fromisoformat(dt_str)
        return dt.strftime("%b %d, %Y %I:%M %p").lstrip("0").replace(" 0", " ")
    except:
        return dt_str

def get_user_by_token(auth_token: str) -> Optional[str]:
    """دریافت ایمیل کاربر از روی توکن"""
    return active_tokens.get(auth_token)

# ============================================================
# APIهای اصلی
# ============================================================

# ----------------------------------------------
# No.1: SIGN IN OR SIGN UP
# POST /api/users/signin
# ----------------------------------------------
@app.post("/api/users/signin", response_model=SignInResponse)
async def sign_in(request: SignInRequest):
    """ورود یا ثبت‌نام کاربر - اگر ایمیل وجود نداشته باشد، کاربر جدید ساخته می‌شود"""
    email = request.userEmailAddress
    password = request.userPassword
    
    # بررسی فرمت رمز عبور (حداقل ۶ کاراکتر شامل حروف و اعداد)
    if not any(c.isalpha() for c in password) or not any(c.isdigit() for c in password):
        raise HTTPException(status_code=400, detail="Password must contain both letters and numbers")
    
    # اگر کاربر جدید است، او را ثبت می‌کنیم
    if email not in users_db:
        users_db[email] = {
            "password": password,
            "auth_token": None,
            "favorites": []
        }
    
    # بررسی رمز عبور
    if users_db[email]["password"] != password:
        raise HTTPException(status_code=401, detail="Invalid password")
    
    # تولید توکن جدید
    token = generate_auth_token()
    users_db[email]["auth_token"] = token
    active_tokens[token] = email
    
    return SignInResponse(
        msg="Sign in successful",
        data={"auth_token": token}
    )

# ----------------------------------------------
# No.2: GET DIARY LIST
# GET /api/diary/list
# ----------------------------------------------
@app.get("/api/diary/list")
async def get_diary_list():
    """دریافت لیست تمام سفرنامه‌ها"""
    diaries = []
    for diary_id, diary_data in diaries_db.items():
        diaries.append({
            "diary_id": diary_id,
            "title": diary_data["title"],
            "publisher_username": diary_data["publisher_username"],
            "thumbnail": diary_data["thumbnail"],
            "publish_datetime": diary_data["publish_datetime"]
        })
    
    # مرتب‌سازی بر اساس تاریخ (جدیدترین اول)
    diaries.sort(key=lambda x: x["publish_datetime"], reverse=True)
    
    return JSONResponse(content={
        "msg": "Success",
        "data": diaries
    })

# ----------------------------------------------
# No.3: GET DIARY DETAIL
# GET /api/diary/{diary_id}
# ----------------------------------------------
@app.get("/api/diary/{diary_id}")
async def get_diary_detail(diary_id: str):
    """دریافت جزئیات کامل یک سفرنامه"""
    if diary_id not in diaries_db:
        raise HTTPException(status_code=404, detail="Diary not found")
    
    diary_data = diaries_db[diary_id]
    
    # پارس کردن main_text اگر JSON string است
    main_text = diary_data["main_text"]
    if isinstance(main_text, str):
        try:
            main_text = json.loads(main_text)
        except:
            pass
    
    return JSONResponse(content={
        "msg": "Success",
        "data": {
            "diary_id": diary_id,
            "title": diary_data["title"],
            "publisher_username": diary_data["publisher_username"],
            "thumbnail": diary_data["thumbnail"],
            "main_text": main_text,
            "publish_datetime": format_datetime(diary_data["publish_datetime"]),
            "images": diary_data.get("images", [])
        }
    })

# ----------------------------------------------
# No.4: GET MY FAVORITES
# GET /api/diary/collection
# ----------------------------------------------
@app.get("/api/diary/collection")
async def get_my_favorites(auth_token: str = Header(...)):
    """دریافت لیست سفرنامه‌های مورد علاقه کاربر - نیاز به auth_token در هدر"""
    email = get_user_by_token(auth_token)
    if not email:
        raise HTTPException(status_code=401, detail="Invalid or expired token")
    
    user_data = users_db[email]
    
    favorites = []
    for fav in user_data["favorites"]:
        diary_id = fav["diary_id"]
        if diary_id in diaries_db:
            diary_data = diaries_db[diary_id]
            favorites.append({
                "diary_id": diary_id,
                "title": diary_data["title"],
                "publisher_username": diary_data["publisher_username"],
                "thumbnail": diary_data["thumbnail"],
                "favorite_datetime": fav["favorite_datetime"]
            })
    
    favorites.sort(key=lambda x: x["favorite_datetime"], reverse=True)
    
    return JSONResponse(content={
        "msg": "Success",
        "data": favorites
    })

# ----------------------------------------------
# ADD TO FAVORITES
# POST /api/diary/favorite
# ----------------------------------------------
@app.post("/api/diary/favorite")
async def add_to_favorites(
    request: Dict[str, Any],
    auth_token: str = Header(...)
):
    """اضافه کردن یک سفرنامه به علاقه‌مندی‌ها"""
    email = get_user_by_token(auth_token)
    if not email:
        raise HTTPException(status_code=401, detail="Invalid or expired token")
    
    diary_id = request.get("diary_id")
    
    if diary_id not in diaries_db:
        raise HTTPException(status_code=404, detail="Diary not found")
    
    user_data = users_db[email]
    for fav in user_data["favorites"]:
        if fav["diary_id"] == diary_id:
            return JSONResponse(content={"msg": "Already favorited"})
    
    user_data["favorites"].append({
        "diary_id": diary_id,
        "favorite_datetime": datetime.now().isoformat()
    })
    
    return JSONResponse(content={
        "msg": "Added to favorites",
        "data": {"diary_id": diary_id}
    })

# ----------------------------------------------
# REMOVE FROM FAVORITES
# DELETE /api/diary/favorite/{diary_id}
# ----------------------------------------------
@app.delete("/api/diary/favorite/{diary_id}")
async def remove_from_favorites(
    diary_id: str,
    auth_token: str = Header(...)
):
    """حذف یک سفرنامه از علاقه‌مندی‌ها"""
    email = get_user_by_token(auth_token)
    if not email:
        raise HTTPException(status_code=401, detail="Invalid or expired token")
    
    user_data = users_db[email]
    original_length = len(user_data["favorites"])
    user_data["favorites"] = [fav for fav in user_data["favorites"] if fav["diary_id"] != diary_id]
    
    if len(user_data["favorites"]) == original_length:
        raise HTTPException(status_code=404, detail="Favorite not found")
    
    return JSONResponse(content={
        "msg": "Removed from favorites",
        "data": {"diary_id": diary_id}
    })

# ----------------------------------------------
# GET ATTRACTIONS
# GET /api/attractions
# ----------------------------------------------
@app.get("/api/attractions")
async def get_attractions():
    """دریافت اطلاعات جاذبه‌های گردشگری"""
    return JSONResponse(content={"msg": "Success", "data": attractions_db})

# ----------------------------------------------
# GET ATTRACTION DETAIL
# GET /api/attractions/{attraction_id}
# ----------------------------------------------
@app.get("/api/attractions/{attraction_id}")
async def get_attraction_detail(attraction_id: int):
    """دریافت جزئیات یک جاذبه گردشگری"""
    for attraction in attractions_db:
        if attraction["id"] == attraction_id:
            return JSONResponse(content={"msg": "Success", "data": attraction})
    
    raise HTTPException(status_code=404, detail="Attraction not found")

# ----------------------------------------------
# GET RESOURCE FILE
# GET /api/{path:path}
# ----------------------------------------------
@app.get("/api/{path:path}")
async def get_resource_file(path: str):
    """دریافت فایل‌های منبع (تصاویر، JSON، و غیره)"""
    file_path = Path(f"static/{path}")
    
    if not file_path.exists():
        raise HTTPException(status_code=404, detail=f"Resource not found: {path}")
    
    if file_path.suffix.lower() in ['.jpg', '.jpeg']:
        media_type = "image/jpeg"
    elif file_path.suffix.lower() == '.png':
        media_type = "image/png"
    else:
        media_type = "application/octet-stream"
    
    return FileResponse(file_path, media_type=media_type)

# ----------------------------------------------
# USER AGREEMENT
# GET /api/user-agreement
# ----------------------------------------------
@app.get("/api/user-agreement", response_class=HTMLResponse)
async def user_agreement():
    """صفحه توافق‌نامه کاربری"""
    return """
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>User Agreement - My France</title>
        <style>
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body {
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                min-height: 100vh;
                display: flex;
                justify-content: center;
                align-items: center;
                padding: 20px;
            }
            .container {
                max-width: 800px;
                width: 100%;
                background: white;
                border-radius: 20px;
                box-shadow: 0 20px 60px rgba(0,0,0,0.3);
                overflow: hidden;
            }
            .header {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 30px;
                text-align: center;
            }
            .header h1 { font-size: 28px; margin-bottom: 10px; }
            .content {
                padding: 30px;
                max-height: 60vh;
                overflow-y: auto;
            }
            .content h2 {
                color: #667eea;
                font-size: 20px;
                margin-top: 20px;
                margin-bottom: 10px;
            }
            .content p {
                color: #333;
                line-height: 1.6;
                margin-bottom: 15px;
            }
            .footer {
                padding: 20px 30px;
                border-top: 1px solid #eee;
                text-align: center;
            }
            .close-btn {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                border: none;
                padding: 12px 30px;
                border-radius: 25px;
                font-size: 16px;
                cursor: pointer;
            }
            .date {
                text-align: right;
                color: #888;
                font-size: 12px;
                margin-top: 20px;
                padding-top: 20px;
                border-top: 1px solid #eee;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="header">
                <h1>📜 User Agreement</h1>
                <p>My France - Travel Diary Application</p>
            </div>
            <div class="content">
                <h2>1. Introduction</h2>
                <p>Welcome to My France ("Application"). By using this Application, you agree to comply with and be bound by the following terms and conditions of use.</p>
                
                <h2>2. Data Collection</h2>
                <p>We collect and store your email address and encrypted password. Your favorite diaries are associated with your account and stored securely.</p>
                
                <h2>3. User Conduct</h2>
                <p>You agree to use the Application only for lawful purposes.</p>
                
                <h2>4. Intellectual Property</h2>
                <p>All content in this Application is protected by copyright and belongs to French Travel.</p>
                
                <h2>5. Contact</h2>
                <p>For any questions, please contact support@french-travel.com</p>
                
                <div class="date">Effective Date: August 1, 2024</div>
            </div>
            <div class="footer">
                <button class="close-btn" onclick="window.close()">✓ I Agree & Close</button>
            </div>
        </div>
    </body>
    </html>
    """

# ----------------------------------------------
# SIGNOUT
# POST /api/users/signout
# ----------------------------------------------
@app.post("/api/users/signout")
async def sign_out(auth_token: str = Header(...)):
    """خروج از حساب کاربری"""
    email = get_user_by_token(auth_token)
    if email:
        users_db[email]["auth_token"] = None
        if auth_token in active_tokens:
            del active_tokens[auth_token]
    
    return JSONResponse(content={"msg": "Sign out successful"})

# ============================================================
# ریشه اصلی
# ============================================================
@app.get("/")
async def root():
    return {
        "message": "My France API - WorldSkills 2024",
        "status": "running",
        "statistics": {
            "total_diaries": len(diaries_db),
            "total_attractions": len(attractions_db),
            "total_users": len(users_db)
        },
        "endpoints": {
            "POST /api/users/signin": "Sign in or sign up",
            "POST /api/users/signout": "Sign out",
            "GET /api/diary/list": "Get all diaries",
            "GET /api/diary/{diary_id}": "Get diary details",
            "GET /api/diary/collection": "Get my favorites",
            "POST /api/diary/favorite": "Add to favorites",
            "DELETE /api/diary/favorite/{diary_id}": "Remove from favorites",
            "GET /api/attractions": "Get all attractions",
            "GET /api/attractions/{id}": "Get attraction details",
            "GET /api/{path}": "Get resource files",
            "GET /api/user-agreement": "User agreement"
        }
    }

# ============================================================
# راه‌اندازی سرور
# ============================================================
if __name__ == "__main__":
    import uvicorn
    print("=" * 60)
    print("🚀 My France API Server - WorldSkills 2024")
    print("=" * 60)
    print(f"📊 Statistics:")
    print(f"   - {len(diaries_db)} Diaries")
    print(f"   - {len(attractions_db)} Attractions")
    print(f"   - {len(users_db)} Users")
    print("-" * 60)
    print("📍 Server running at: http://localhost:8000")
    print("📖 API Docs: http://localhost:8000/docs")
    print("=" * 60)
    uvicorn.run(app, host="0.0.0.0", port=8000)