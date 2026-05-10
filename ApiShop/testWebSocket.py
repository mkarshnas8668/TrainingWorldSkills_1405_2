# test_main_websocket.py
import asyncio
import websockets
import json

async def test_main():
    # تست WebSocket پروژه اصلی
    uri = "ws://127.0.0.1:8000/ws/user/7"
    
    print(f"🔄 اتصال به {uri}...")
    
    async with websockets.connect(uri) as ws:
        print("✅ متصل شد!")
        
        # دریافت welcome
        welcome = await ws.recv()
        print(f"📩 Welcome: {welcome}")
        
        # ارسال چت
        await ws.send(json.dumps({
            "type": "chat",
            "receiver_id": "admin_admin",
            "message": "سلام از کاربر 7!"
        }))
        print("📤 پیام چت ارسال شد")
        
        await asyncio.sleep(2)
        print("✅ تست با موفقیت انجام شد!")

asyncio.run(test_main())