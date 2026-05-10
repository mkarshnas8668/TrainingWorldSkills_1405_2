# client_test.py
import asyncio
import websockets
import json

async def test():
    uri = "ws://127.0.0.1:8000/ws/test_user_123"
    
    print(f"🔄 Connecting to {uri}...")
    
    try:
        async with websockets.connect(uri) as ws:
            print("✅ Connected!")
            
            # دریافت welcome message
            response = await ws.recv()
            print(f"📩 Server says: {response}")
            
            # ارسال پیام
            message = {
                "type": "chat",
                "message": "Hello Server!"
            }
            await ws.send(json.dumps(message))
            print(f"📤 Sent: {message}")
            
            # دریافت پاسخ
            response = await ws.recv()
            print(f"📩 Server response: {response}")
            
            print("✅ Test completed successfully!")
            
    except websockets.exceptions.InvalidStatusCode as e:
        print(f"❌ Connection failed with status: {e.status_code}")
        print("Make sure the server is running and supports WebSocket!")
    except Exception as e:
        print(f"❌ Error: {type(e).__name__}: {e}")

if __name__ == "__main__":
    asyncio.run(test())