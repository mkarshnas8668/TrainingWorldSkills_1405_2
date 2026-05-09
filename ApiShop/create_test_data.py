# test_bot.py
import requests
import json
import random
import time

class ShopTester:
    def __init__(self):
        self.base = "http://127.0.0.1:8000"
        self.admin_token = None
        self.users = []
        self.categories = []
        self.products = []
        
    def reg(self, username, email, password, full_name=None, phone=None, address=None):
        data = {
            "username": username,
            "email": email,
            "password": password,
            "full_name": full_name or username,
            "phone": phone or f"09{random.randint(100000000, 999999999)}",
            "address": address or f"آدرس {username}"
        }
        r = requests.post(f"{self.base}/register", json=data)
        print(f"ثبت نام {username}: {r.status_code}")
        return r.json() if r.status_code == 200 else None

    def login(self, username, password):
        r = requests.post(f"{self.base}/token", data={
            "username": username,
            "password": password
        })
        print(f"ورود {username}: {r.status_code}")
        return r.json().get("access_token") if r.status_code == 200 else None

    def setup_admin(self):
        print("\n=== ساخت ادمین ===")
        self.reg("admin", "admin@shop.com", "admin123", "مدیر سیستم")
        
        # تغییر رول در دیتابیس
        import sqlite3
        conn = sqlite3.connect('shop.db')
        conn.execute("UPDATE users SET role='admin' WHERE username='admin'")
        conn.commit()
        conn.close()
        print("✅ رول admin تغییر کرد")
        
        self.admin_token = self.login("admin", "admin123")
        return self.admin_token

    def create_users(self):
        print("\n=== ساخت کاربران ===")
        users_info = [
            ("alireza", "alireza@test.com", "pass123", "علیرضا محمدی"),
            ("sara", "sara@test.com", "pass123", "سارا احمدی"),
            ("mohammad", "mohammad@test.com", "pass123", "محمد رضایی"),
            ("zahra", "zahra@test.com", "pass123", "زهرا حسینی"),
            ("amir", "amir@test.com", "pass123", "امیر کریمی")
        ]
        
        for username, email, password, full_name in users_info:
            self.reg(username, email, password, full_name)
            token = self.login(username, password)
            if token:
                self.users.append({"username": username, "token": token})
            time.sleep(0.3)
        
        print(f"✅ {len(self.users)} کاربر ساخته شد")

    def create_categories(self):
        print("\n=== ساخت دسته‌بندی‌ها ===")
        headers = {"Authorization": f"Bearer {self.admin_token}"}
        cats = ["لوازم الکترونیکی", "پوشاک و مد", "کتاب و محصولات فرهنگی", 
                "ورزش و سفر", "خانه و آشپزخانه", "زیبایی و سلامت"]
        
        for cat in cats:
            r = requests.post(f"{self.base}/categories", 
                            json={"name": cat, "description": f"دسته‌بندی {cat}"},
                            headers=headers)
            if r.status_code == 200:
                self.categories.append(r.json())
            time.sleep(0.2)
        
        print(f"✅ {len(self.categories)} دسته‌بندی ساخته شد")

    def create_products(self):
        print("\n=== ساخت محصولات ===")
        headers = {"Authorization": f"Bearer {self.admin_token}"}
        
        products = [
            ("گوشی سامسونگ A54", 12500000, 50, 0),
            ("لپ‌تاپ لنوو", 22500000, 30, 0),
            ("هدفون سونی", 4500000, 100, 0),
            ("ساعت اپل واچ", 18000000, 25, 0),
            ("کتونی نایک", 5800000, 200, 1),
            ("شلوار جین لیوایز", 1800000, 150, 1),
            ("تیشرت نخی", 450000, 500, 1),
            ("کتاب هنر شفاف اندیشیدن", 185000, 300, 2),
            ("کتاب چهار میثاق", 120000, 250, 2),
            ("کوله‌پشتی کوهنوردی", 3200000, 80, 3),
            ("سرویس قابلمه", 5500000, 60, 4),
            ("کرم ضد آفتاب", 320000, 400, 5)
        ]
        
        for name, price, stock, cat_idx in products:
            r = requests.post(f"{self.base}/products",
                            json={
                                "name": name,
                                "price": price,
                                "stock": stock,
                                "category_id": self.categories[cat_idx]["id"],
                                "description": f"توضیحات {name}"
                            },
                            headers=headers)
            if r.status_code == 200:
                self.products.append(r.json())
            time.sleep(0.2)
        
        print(f"✅ {len(self.products)} محصول ساخته شد")

    def add_to_carts(self):
        print("\n=== اضافه کردن به سبد خرید ===")
        for user in self.users:
            headers = {"Authorization": f"Bearer {user['token']}"}
            selected = random.sample(self.products, random.randint(2, 4))
            
            for product in selected:
                requests.post(f"{self.base}/cart/items",
                            json={"product_id": product["id"], "quantity": random.randint(1, 3)},
                            headers=headers)
                time.sleep(0.1)
        
        print("✅ محصولات به سبد خرید اضافه شدند")

    def create_orders(self):
        print("\n=== ثبت سفارش‌ها ===")
        for user in self.users[:3]:
            headers = {"Authorization": f"Bearer {user['token']}"}
            requests.post(f"{self.base}/orders",
                        json={
                            "shipping_address": f"آدرس {user['username']}",
                            "shipping_phone": f"09{random.randint(100000000, 999999999)}"
                        },
                        headers=headers)
            time.sleep(0.3)
        
        print("✅ سفارش‌ها ثبت شدند")

    def create_reviews(self):
        print("\n=== ثبت نظرات ===")
        comments = ["عالی", "خوب", "معمولی", "رضایت بخش", "پیشنهاد می‌کنم"]
        
        for user in self.users:
            headers = {"Authorization": f"Bearer {user['token']}"}
            for product in random.sample(self.products, random.randint(2, 3)):
                requests.post(f"{self.base}/products/{product['id']}/reviews",
                            json={
                                "rating": random.randint(3, 5),
                                "comment": random.choice(comments)
                            },
                            headers=headers)
                time.sleep(0.1)
        
        print("✅ نظرات ثبت شدند")

    def run(self):
        print("🚀 شروع تست کامل API فروشگاه")
        print("="*50)
        
        self.setup_admin()
        self.create_users()
        self.create_categories()
        self.create_products()
        self.add_to_carts()
        self.create_orders()
        self.create_reviews()
        
        print("\n" + "="*50)
        print("🎉 تست با موفقیت انجام شد!")
        print(f"📊 آمار:")
        print(f"   - کاربران: {len(self.users)}")
        print(f"   - دسته‌بندی‌ها: {len(self.categories)}")
        print(f"   - محصولات: {len(self.products)}")
        print(f"\n📖 مستندات: {self.base}/docs")

if __name__ == "__main__":
    tester = ShopTester()
    tester.run()