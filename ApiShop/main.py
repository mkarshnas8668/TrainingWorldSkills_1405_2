# main.py
from fastapi import FastAPI, Depends, HTTPException, UploadFile, File, Form, status, WebSocket, WebSocketDisconnect
from fastapi.staticfiles import StaticFiles
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm
from fastapi.responses import StreamingResponse
from sqlalchemy import create_engine, Column, Integer, String, Float, Text, DateTime, Boolean, ForeignKey
from sqlalchemy.orm import declarative_base, sessionmaker, Session, relationship
from sqlalchemy.sql import func
from jose import JWTError, jwt
from pydantic import BaseModel
from datetime import datetime, timedelta
from typing import List, Optional, Dict, Set
import os
import uuid
import shutil
import hashlib
import secrets
from enum import Enum
import asyncio
import json

# ============= تنظیمات اولیه =============
DATABASE_URL = "sqlite:///./shop.db"
SECRET_KEY = "your-secret-key-change-in-production"
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 30
UPLOAD_DIR = "uploads"

# ساخت پوشه آپلود اگر وجود ندارد
os.makedirs(UPLOAD_DIR, exist_ok=True)

# ============= دیتابیس =============
engine = create_engine(DATABASE_URL, connect_args={"check_same_thread": False})
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

# ============= توابع هش کردن پسورد =============
def hash_password(password: str) -> str:
    salt = secrets.token_hex(16)
    salted_password = password + salt
    hashed = hashlib.sha256(salted_password.encode()).hexdigest()
    return f"{salt}${hashed}"

def verify_password(plain_password: str, hashed_password: str) -> bool:
    try:
        salt, stored_hash = hashed_password.split('$')
        salted_password = plain_password + salt
        new_hash = hashlib.sha256(salted_password.encode()).hexdigest()
        return new_hash == stored_hash
    except:
        return False

# ============= توابع JWT =============
def create_access_token(data: dict):
    to_encode = data.copy()
    expire = datetime.utcnow() + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt

def decode_token(token: str):
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        return payload.get("sub")
    except:
        return None

# ============= مدل‌های دیتابیس =============
class UserRole(str, Enum):
    CUSTOMER = "customer"
    ADMIN = "admin"

class OrderStatus(str, Enum):
    PENDING = "pending"
    PROCESSING = "processing"
    SHIPPED = "shipped"
    DELIVERED = "delivered"
    CANCELLED = "cancelled"

class User(Base):
    __tablename__ = "users"
    
    id = Column(Integer, primary_key=True, index=True)
    username = Column(String(50), unique=True, index=True, nullable=False)
    email = Column(String(100), unique=True, index=True, nullable=False)
    hashed_password = Column(String(200), nullable=False)
    full_name = Column(String(100), nullable=True)
    phone = Column(String(20), nullable=True)
    address = Column(Text, nullable=True)
    role = Column(String(20), default=UserRole.CUSTOMER.value)
    is_active = Column(Boolean, default=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())
    
    orders = relationship("Order", back_populates="user")
    cart = relationship("Cart", back_populates="user", uselist=False)

class Category(Base):
    __tablename__ = "categories"
    
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(100), unique=True, nullable=False)
    description = Column(Text, nullable=True)
    image_url = Column(String(500), nullable=True)
    parent_id = Column(Integer, ForeignKey("categories.id"), nullable=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    
    products = relationship("Product", back_populates="category")

class Product(Base):
    __tablename__ = "products"
    
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(200), nullable=False)
    description = Column(Text, nullable=True)
    price = Column(Float, nullable=False)
    discount_price = Column(Float, nullable=True)
    stock = Column(Integer, default=0)
    sku = Column(String(50), unique=True, nullable=True)
    is_available = Column(Boolean, default=True)
    category_id = Column(Integer, ForeignKey("categories.id"))
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())
    
    category = relationship("Category", back_populates="products")
    images = relationship("ProductImage", back_populates="product", cascade="all, delete-orphan")
    reviews = relationship("Review", back_populates="product", cascade="all, delete-orphan")

class ProductImage(Base):
    __tablename__ = "product_images"
    
    id = Column(Integer, primary_key=True, index=True)
    product_id = Column(Integer, ForeignKey("products.id"), nullable=False)
    image_url = Column(String(500), nullable=False)
    is_main = Column(Boolean, default=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    
    product = relationship("Product", back_populates="images")

class Review(Base):
    __tablename__ = "reviews"
    
    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    product_id = Column(Integer, ForeignKey("products.id"), nullable=False)
    rating = Column(Integer, nullable=False)
    comment = Column(Text, nullable=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    
    user = relationship("User")
    product = relationship("Product", back_populates="reviews")

class Cart(Base):
    __tablename__ = "carts"
    
    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), unique=True, nullable=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    
    user = relationship("User", back_populates="cart")
    items = relationship("CartItem", back_populates="cart", cascade="all, delete-orphan")

class CartItem(Base):
    __tablename__ = "cart_items"
    
    id = Column(Integer, primary_key=True, index=True)
    cart_id = Column(Integer, ForeignKey("carts.id"), nullable=False)
    product_id = Column(Integer, ForeignKey("products.id"), nullable=False)
    quantity = Column(Integer, default=1)
    
    cart = relationship("Cart", back_populates="items")
    product = relationship("Product")

class Order(Base):
    __tablename__ = "orders"
    
    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    status = Column(String(20), default=OrderStatus.PENDING.value)
    total_amount = Column(Float, nullable=False)
    shipping_address = Column(Text, nullable=False)
    shipping_phone = Column(String(20), nullable=False)
    notes = Column(Text, nullable=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())
    
    user = relationship("User", back_populates="orders")
    items = relationship("OrderItem", back_populates="order", cascade="all, delete-orphan")

class OrderItem(Base):
    __tablename__ = "order_items"
    
    id = Column(Integer, primary_key=True, index=True)
    order_id = Column(Integer, ForeignKey("orders.id"), nullable=False)
    product_id = Column(Integer, ForeignKey("products.id"), nullable=False)
    quantity = Column(Integer, nullable=False)
    price_at_time = Column(Float, nullable=False)
    
    order = relationship("Order", back_populates="items")
    product = relationship("Product")

# ============= Pydantic Models =============
class UserCreate(BaseModel):
    username: str
    email: str
    password: str
    full_name: Optional[str] = None
    phone: Optional[str] = None
    address: Optional[str] = None

class UserUpdate(BaseModel):
    full_name: Optional[str] = None
    phone: Optional[str] = None
    address: Optional[str] = None
    email: Optional[str] = None

class UserResponse(BaseModel):
    id: int
    username: str
    email: str
    full_name: Optional[str] = None
    phone: Optional[str] = None
    address: Optional[str] = None
    role: str
    is_active: bool
    created_at: datetime
    
    class Config:
        from_attributes = True

class CategoryCreate(BaseModel):
    name: str
    description: Optional[str] = None
    parent_id: Optional[int] = None

class CategoryResponse(BaseModel):
    id: int
    name: str
    description: Optional[str] = None
    image_url: Optional[str] = None
    parent_id: Optional[int] = None
    
    class Config:
        from_attributes = True

class ProductCreate(BaseModel):
    name: str
    description: Optional[str] = None
    price: float
    discount_price: Optional[float] = None
    stock: int = 0
    sku: Optional[str] = None
    category_id: int
    is_available: bool = True

class ProductUpdate(BaseModel):
    name: Optional[str] = None
    description: Optional[str] = None
    price: Optional[float] = None
    discount_price: Optional[float] = None
    stock: Optional[int] = None
    sku: Optional[str] = None
    category_id: Optional[int] = None
    is_available: Optional[bool] = None

class ProductResponse(BaseModel):
    id: int
    name: str
    description: Optional[str] = None
    price: float
    discount_price: Optional[float] = None
    stock: int
    sku: Optional[str] = None
    is_available: bool
    category_id: int
    created_at: datetime
    
    class Config:
        from_attributes = True

class CartItemCreate(BaseModel):
    product_id: int
    quantity: int = 1

class CartItemUpdate(BaseModel):
    quantity: int

class CartItemResponse(BaseModel):
    id: int
    product_id: int
    quantity: int
    product: ProductResponse
    
    class Config:
        from_attributes = True

class CartResponse(BaseModel):
    id: int
    user_id: int
    items: List[CartItemResponse] = []
    
    class Config:
        from_attributes = True

class OrderCreate(BaseModel):
    shipping_address: str
    shipping_phone: str
    notes: Optional[str] = None

class OrderStatusUpdate(BaseModel):
    status: OrderStatus

class OrderItemResponse(BaseModel):
    id: int
    product_id: int
    quantity: int
    price_at_time: float
    product: ProductResponse
    
    class Config:
        from_attributes = True

class OrderResponse(BaseModel):
    id: int
    user_id: int
    status: str
    total_amount: float
    shipping_address: str
    shipping_phone: str
    notes: Optional[str] = None
    created_at: datetime
    items: List[OrderItemResponse] = []
    
    class Config:
        from_attributes = True

class ReviewCreate(BaseModel):
    rating: int
    comment: Optional[str] = None

class ReviewResponse(BaseModel):
    id: int
    user_id: int
    product_id: int
    rating: int
    comment: Optional[str] = None
    created_at: datetime
    
    class Config:
        from_attributes = True

class Token(BaseModel):
    access_token: str
    token_type: str

class TokenData(BaseModel):
    username: Optional[str] = None

# ============= توابع کمکی =============
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

def save_upload_file(upload_file: UploadFile, subfolder: str = ""):
    folder_path = os.path.join(UPLOAD_DIR, subfolder)
    os.makedirs(folder_path, exist_ok=True)
    
    file_extension = os.path.splitext(upload_file.filename)[1] if upload_file.filename else ".jpg"
    file_name = f"{uuid.uuid4()}{file_extension}"
    file_path = os.path.join(folder_path, file_name)
    
    with open(file_path, "wb") as buffer:
        shutil.copyfileobj(upload_file.file, buffer)
    
    return f"/uploads/{subfolder}/{file_name}"

def get_current_user(token: str = Depends(oauth2_scheme), db: Session = Depends(get_db)):
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        username: str = payload.get("sub")
        if username is None:
            raise credentials_exception
        token_data = TokenData(username=username)
    except JWTError:
        raise credentials_exception
    
    user = db.query(User).filter(User.username == token_data.username).first()
    if user is None:
        raise credentials_exception
    return user

def get_current_admin(current_user: User = Depends(get_current_user)):
    if current_user.role != UserRole.ADMIN.value:
        raise HTTPException(status_code=403, detail="Not enough permissions")
    return current_user

# ============= WebSocket Connection Manager =============
class ConnectionManager:
    def __init__(self):
        self.active_connections: Dict[str, WebSocket] = {}
        self.online_users: Set[str] = set()
    
    async def connect(self, websocket: WebSocket, client_id: str):
        await websocket.accept()
        self.active_connections[client_id] = websocket
        self.online_users.add(client_id)
        await self.broadcast_online_users()
        
        if client_id != "admin":
            await self.notify_admin({
                "type": "user_online",
                "user_id": client_id,
                "message": f"کاربر {client_id} آنلاین شد"
            })
    
    async def disconnect(self, client_id: str):
        if client_id in self.active_connections:
            del self.active_connections[client_id]
            self.online_users.discard(client_id)
            await self.broadcast_online_users()
            
            if client_id != "admin":
                await self.notify_admin({
                    "type": "user_offline",
                    "user_id": client_id,
                    "message": f"کاربر {client_id} آفلاین شد"
                })
    
    async def send_personal_message(self, message: dict, client_id: str):
        if client_id in self.active_connections:
            try:
                await self.active_connections[client_id].send_json(message)
            except:
                await self.disconnect(client_id)
    
    async def broadcast_to_users(self, message: dict):
        for client_id, connection in self.active_connections.items():
            if client_id != "admin":
                try:
                    await connection.send_json(message)
                except:
                    pass
    
    async def notify_admin(self, message: dict):
        if "admin" in self.active_connections:
            try:
                await self.active_connections["admin"].send_json(message)
            except:
                pass
    
    async def broadcast_online_users(self):
        users = [uid for uid in self.online_users if uid != "admin"]
        for client_id in self.active_connections:
            try:
                await self.active_connections[client_id].send_json({
                    "type": "online_users",
                    "users": users,
                    "count": len(users)
                })
            except:
                pass
    
    async def send_chat_message(self, sender_id: str, receiver_id: str, message: str):
        chat_message = {
            "type": "chat_message",
            "sender_id": sender_id,
            "message": message,
            "timestamp": datetime.now().isoformat()
        }
        
        await self.send_personal_message(chat_message, receiver_id)
        await self.send_personal_message({**chat_message, "status": "sent"}, sender_id)

manager = ConnectionManager()

# ============= SSE (Server-Sent Events) =============
sse_clients: Dict[str, asyncio.Queue] = {}

# ============= FastAPI App =============
app = FastAPI(title="Shop API", version="1.0.0")

# ============= سیستم احراز هویت =============
@app.post("/register", response_model=UserResponse, tags=["Authentication"])
def register(user: UserCreate, db: Session = Depends(get_db)):
    db_user = db.query(User).filter(User.username == user.username).first()
    if db_user:
        raise HTTPException(status_code=400, detail="Username already registered")
    
    db_email = db.query(User).filter(User.email == user.email).first()
    if db_email:
        raise HTTPException(status_code=400, detail="Email already registered")
    
    hashed_password = hash_password(user.password)
    db_user = User(
        username=user.username,
        email=user.email,
        hashed_password=hashed_password,
        full_name=user.full_name,
        phone=user.phone,
        address=user.address,
        role=UserRole.CUSTOMER.value
    )
    db.add(db_user)
    db.commit()
    db.refresh(db_user)
    
    cart = Cart(user_id=db_user.id)
    db.add(cart)
    db.commit()
    
    return db_user

@app.post("/token", response_model=Token, tags=["Authentication"])
def login(form_data: OAuth2PasswordRequestForm = Depends(), db: Session = Depends(get_db)):
    user = db.query(User).filter(User.username == form_data.username).first()
    if not user or not verify_password(form_data.password, user.hashed_password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    access_token = create_access_token(data={"sub": user.username})
    return {"access_token": access_token, "token_type": "bearer"}

@app.get("/users/me", response_model=UserResponse, tags=["Users"])
def read_users_me(current_user: User = Depends(get_current_user)):
    return current_user

@app.put("/users/me", response_model=UserResponse, tags=["Users"])
def update_user(user_update: UserUpdate, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    for key, value in user_update.dict(exclude_unset=True).items():
        setattr(current_user, key, value)
    db.commit()
    db.refresh(current_user)
    return current_user

# ============= مدیریت کاربران (ادمین) =============
@app.get("/admin/users", response_model=List[UserResponse], tags=["Admin - Users"])
def list_users(current_user: User = Depends(get_current_admin), db: Session = Depends(get_db), skip: int = 0, limit: int = 100):
    users = db.query(User).offset(skip).limit(limit).all()
    return users

@app.put("/admin/users/{user_id}/role", tags=["Admin - Users"])
def change_user_role(user_id: int, role: str, current_user: User = Depends(get_current_admin), db: Session = Depends(get_db)):
    if role not in [UserRole.CUSTOMER.value, UserRole.ADMIN.value]:
        raise HTTPException(status_code=400, detail="Invalid role")
    
    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    
    user.role = role
    db.commit()
    return {"message": f"User role changed to {role}"}

# ============= مدیریت دسته‌بندی‌ها =============
@app.post("/categories", response_model=CategoryResponse, tags=["Categories"])
def create_category(category: CategoryCreate, current_user: User = Depends(get_current_admin), db: Session = Depends(get_db)):
    db_category = Category(**category.dict())
    db.add(db_category)
    db.commit()
    db.refresh(db_category)
    return db_category

@app.get("/categories", response_model=List[CategoryResponse], tags=["Categories"])
def list_categories(db: Session = Depends(get_db), skip: int = 0, limit: int = 100):
    categories = db.query(Category).offset(skip).limit(limit).all()
    return categories

@app.get("/categories/{category_id}", response_model=CategoryResponse, tags=["Categories"])
def get_category(category_id: int, db: Session = Depends(get_db)):
    category = db.query(Category).filter(Category.id == category_id).first()
    if not category:
        raise HTTPException(status_code=404, detail="Category not found")
    return category

@app.put("/categories/{category_id}", response_model=CategoryResponse, tags=["Categories"])
def update_category(category_id: int, category_update: CategoryCreate, current_user: User = Depends(get_current_admin), db: Session = Depends(get_db)):
    category = db.query(Category).filter(Category.id == category_id).first()
    if not category:
        raise HTTPException(status_code=404, detail="Category not found")
    for key, value in category_update.dict().items():
        setattr(category, key, value)
    db.commit()
    db.refresh(category)
    return category

@app.delete("/categories/{category_id}", tags=["Categories"])
def delete_category(category_id: int, current_user: User = Depends(get_current_admin), db: Session = Depends(get_db)):
    category = db.query(Category).filter(Category.id == category_id).first()
    if not category:
        raise HTTPException(status_code=404, detail="Category not found")
    db.delete(category)
    db.commit()
    return {"message": "Category deleted successfully"}

# ============= مدیریت محصولات =============
@app.post("/products", response_model=ProductResponse, tags=["Products"])
def create_product(product: ProductCreate, current_user: User = Depends(get_current_admin), db: Session = Depends(get_db)):
    db_product = Product(**product.dict())
    db.add(db_product)
    db.commit()
    db.refresh(db_product)
    return db_product

@app.get("/products", response_model=List[ProductResponse], tags=["Products"])
def list_products(
    skip: int = 0,
    limit: int = 100,
    category_id: Optional[int] = None,
    search: Optional[str] = None,
    min_price: Optional[float] = None,
    max_price: Optional[float] = None,
    db: Session = Depends(get_db)
):
    query = db.query(Product)
    
    if category_id:
        query = query.filter(Product.category_id == category_id)
    if search:
        query = query.filter(Product.name.contains(search))
    if min_price is not None:
        query = query.filter(Product.price >= min_price)
    if max_price is not None:
        query = query.filter(Product.price <= max_price)
    
    products = query.offset(skip).limit(limit).all()
    return products

@app.get("/products/{product_id}", response_model=ProductResponse, tags=["Products"])
def get_product(product_id: int, db: Session = Depends(get_db)):
    product = db.query(Product).filter(Product.id == product_id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
    return product

@app.put("/products/{product_id}", response_model=ProductResponse, tags=["Products"])
def update_product(product_id: int, product_update: ProductUpdate, current_user: User = Depends(get_current_admin), db: Session = Depends(get_db)):
    product = db.query(Product).filter(Product.id == product_id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
    for key, value in product_update.dict(exclude_unset=True).items():
        setattr(product, key, value)
    db.commit()
    db.refresh(product)
    return product

@app.delete("/products/{product_id}", tags=["Products"])
def delete_product(product_id: int, current_user: User = Depends(get_current_admin), db: Session = Depends(get_db)):
    product = db.query(Product).filter(Product.id == product_id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
    db.delete(product)
    db.commit()
    return {"message": "Product deleted successfully"}

@app.post("/products/{product_id}/images", tags=["Products"])
def upload_product_image(
    product_id: int,
    file: UploadFile = File(...),
    is_main: bool = Form(False),
    current_user: User = Depends(get_current_admin),
    db: Session = Depends(get_db)
):
    product = db.query(Product).filter(Product.id == product_id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
    
    file_path = save_upload_file(file, "products")
    
    if is_main:
        db.query(ProductImage).filter(
            ProductImage.product_id == product_id,
            ProductImage.is_main == True
        ).update({"is_main": False})
    
    product_image = ProductImage(
        product_id=product_id,
        image_url=file_path,
        is_main=is_main
    )
    db.add(product_image)
    db.commit()
    db.refresh(product_image)
    
    return {"message": "Image uploaded successfully", "image_url": product_image.image_url}

# ============= سبد خرید =============
@app.get("/cart", response_model=CartResponse, tags=["Cart"])
def get_cart(current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    cart = db.query(Cart).filter(Cart.user_id == current_user.id).first()
    if not cart:
        cart = Cart(user_id=current_user.id)
        db.add(cart)
        db.commit()
        db.refresh(cart)
    return cart

@app.post("/cart/items", tags=["Cart"])
def add_to_cart(item: CartItemCreate, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    product = db.query(Product).filter(Product.id == item.product_id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
    if not product.is_available:
        raise HTTPException(status_code=400, detail="Product is not available")
    if product.stock < item.quantity:
        raise HTTPException(status_code=400, detail="Insufficient stock")
    
    cart = db.query(Cart).filter(Cart.user_id == current_user.id).first()
    if not cart:
        cart = Cart(user_id=current_user.id)
        db.add(cart)
        db.commit()
        db.refresh(cart)
    
    existing_item = db.query(CartItem).filter(
        CartItem.cart_id == cart.id,
        CartItem.product_id == item.product_id
    ).first()
    
    if existing_item:
        existing_item.quantity += item.quantity
    else:
        cart_item = CartItem(
            cart_id=cart.id,
            product_id=item.product_id,
            quantity=item.quantity
        )
        db.add(cart_item)
    
    db.commit()
    return {"message": "Item added to cart"}

@app.put("/cart/items/{item_id}", tags=["Cart"])
def update_cart_item(item_id: int, item_update: CartItemUpdate, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    item = db.query(CartItem).join(Cart).filter(
        CartItem.id == item_id,
        Cart.user_id == current_user.id
    ).first()
    
    if not item:
        raise HTTPException(status_code=404, detail="Cart item not found")
    
    if item_update.quantity <= 0:
        db.delete(item)
    else:
        item.quantity = item_update.quantity
    
    db.commit()
    return {"message": "Cart item updated"}

@app.delete("/cart/items/{item_id}", tags=["Cart"])
def remove_from_cart(item_id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    item = db.query(CartItem).join(Cart).filter(
        CartItem.id == item_id,
        Cart.user_id == current_user.id
    ).first()
    
    if not item:
        raise HTTPException(status_code=404, detail="Cart item not found")
    
    db.delete(item)
    db.commit()
    return {"message": "Item removed from cart"}

@app.delete("/cart", tags=["Cart"])
def clear_cart(current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    cart = db.query(Cart).filter(Cart.user_id == current_user.id).first()
    if cart:
        db.query(CartItem).filter(CartItem.cart_id == cart.id).delete()
        db.commit()
    return {"message": "Cart cleared"}

# ============= سفارش‌ها =============
@app.post("/orders", response_model=OrderResponse, tags=["Orders"])
def create_order(order_data: OrderCreate, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    cart = db.query(Cart).filter(Cart.user_id == current_user.id).first()
    if not cart or not cart.items:
        raise HTTPException(status_code=400, detail="Cart is empty")
    
    total_amount = 0
    order_items = []
    
    for cart_item in cart.items:
        product = cart_item.product
        if not product.is_available:
            raise HTTPException(status_code=400, detail=f"Product {product.name} is not available")
        if product.stock < cart_item.quantity:
            raise HTTPException(status_code=400, detail=f"Insufficient stock for {product.name}")
        
        price = product.discount_price if product.discount_price else product.price
        total_amount += price * cart_item.quantity
        
        order_items.append({
            "product_id": product.id,
            "quantity": cart_item.quantity,
            "price_at_time": price
        })
        
        product.stock -= cart_item.quantity
    
    order = Order(
        user_id=current_user.id,
        total_amount=total_amount,
        shipping_address=order_data.shipping_address,
        shipping_phone=order_data.shipping_phone,
        notes=order_data.notes
    )
    db.add(order)
    db.flush()
    
    for item_data in order_items:
        order_item = OrderItem(
            order_id=order.id,
            product_id=item_data["product_id"],
            quantity=item_data["quantity"],
            price_at_time=item_data["price_at_time"]
        )
        db.add(order_item)
    
    db.query(CartItem).filter(CartItem.cart_id == cart.id).delete()
    
    db.commit()
    db.refresh(order)
    return order

@app.get("/orders", response_model=List[OrderResponse], tags=["Orders"])
def list_orders(current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    orders = db.query(Order).filter(Order.user_id == current_user.id).order_by(Order.created_at.desc()).all()
    return orders

@app.get("/orders/{order_id}", response_model=OrderResponse, tags=["Orders"])
def get_order(order_id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    order = db.query(Order).filter(
        Order.id == order_id,
        Order.user_id == current_user.id
    ).first()
    if not order:
        raise HTTPException(status_code=404, detail="Order not found")
    return order

# ============= مدیریت سفارش‌ها (ادمین) =============
@app.get("/admin/orders", response_model=List[OrderResponse], tags=["Admin - Orders"])
def admin_list_orders(current_user: User = Depends(get_current_admin), db: Session = Depends(get_db), skip: int = 0, limit: int = 100):
    orders = db.query(Order).offset(skip).limit(limit).all()
    return orders

@app.put("/admin/orders/{order_id}/status", response_model=OrderResponse, tags=["Admin - Orders"])
def update_order_status(order_id: int, status_update: OrderStatusUpdate, current_user: User = Depends(get_current_admin), db: Session = Depends(get_db)):
    order = db.query(Order).filter(Order.id == order_id).first()
    if not order:
        raise HTTPException(status_code=404, detail="Order not found")
    order.status = status_update.status.value
    db.commit()
    db.refresh(order)
    return order

# ============= نظرات و امتیازات =============
@app.post("/products/{product_id}/reviews", response_model=ReviewResponse, tags=["Reviews"])
def create_review(product_id: int, review: ReviewCreate, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    product = db.query(Product).filter(Product.id == product_id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
    
    if review.rating < 1 or review.rating > 5:
        raise HTTPException(status_code=400, detail="Rating must be between 1 and 5")
    
    db_review = Review(
        user_id=current_user.id,
        product_id=product_id,
        rating=review.rating,
        comment=review.comment
    )
    db.add(db_review)
    db.commit()
    db.refresh(db_review)
    return db_review

@app.get("/products/{product_id}/reviews", response_model=List[ReviewResponse], tags=["Reviews"])
def list_reviews(product_id: int, db: Session = Depends(get_db)):
    reviews = db.query(Review).filter(Review.product_id == product_id).all()
    return reviews

# ============= WebSocket Endpoints =============
@app.websocket("/ws/{client_type}/{client_id}")
async def websocket_endpoint(websocket: WebSocket, client_type: str, client_id: str):
    client_identifier = f"{client_type}_{client_id}"
    
    try:
        await manager.connect(websocket, client_identifier)
        
        await manager.send_personal_message({
            "type": "welcome",
            "message": f"خوش آمدید {client_identifier}!",
            "online_users": [uid for uid in manager.online_users if uid != "admin"]
        }, client_identifier)
        
        while True:
            data = await websocket.receive_json()
            message_type = data.get("type")
            
            if message_type == "chat":
                receiver = data.get("receiver_id", "admin")
                message = data.get("message", "")
                
                await manager.send_chat_message(
                    sender_id=client_identifier,
                    receiver_id=receiver,
                    message=message
                )
                
            elif message_type == "typing":
                await manager.send_personal_message({
                    "type": "typing",
                    "user_id": client_identifier,
                    "is_typing": data.get("is_typing", True)
                }, data.get("receiver_id", "admin"))
                
            elif message_type == "get_online_users":
                await manager.send_personal_message({
                    "type": "online_users",
                    "users": [uid for uid in manager.online_users if uid != "admin"]
                }, client_identifier)
    
    except WebSocketDisconnect:
        await manager.disconnect(client_identifier)
        print(f"❌ {client_identifier} قطع شد")
    
    except Exception as e:
        print(f"❌ خطا: {e}")
        await manager.disconnect(client_identifier)

@app.get("/ws/online-users", tags=["WebSocket"])
async def get_online_users():
    users = [uid for uid in manager.online_users if uid != "admin"]
    return {"online_users": users, "count": len(users)}

@app.post("/ws/broadcast", tags=["WebSocket"])
async def broadcast_message(message: str, current_user: User = Depends(get_current_admin)):
    await manager.broadcast_to_users({
        "type": "broadcast",
        "message": message,
        "from": "admin",
        "timestamp": datetime.now().isoformat()
    })
    return {"message": "پیام با موفقیت ارسال شد"}

# ============= SSE (Server-Sent Events) Endpoints =============
@app.get("/sse/subscribe/{client_id}")
async def sse_subscribe(client_id: str, token: str = None):
    """
    SSE endpoint - کلاینت وصل میشه و منتظر پیام می‌مونه
    
    آدرس: GET /sse/subscribe/user_7?token=xxx
    
    فرمت پیام:
    data: {"type": "notification", "message": "سلام", "title": "پیام جدید"}\n\n
    """
    
    # ساخت صف برای این کلاینت
    queue = asyncio.Queue()
    sse_clients[client_id] = queue
    
    print(f"🔵 SSE client connected: {client_id}")
    
    async def event_generator():
        try:
            # اول یه پیام خوش‌آمد بفرست
            yield f"data: {json.dumps({'type': 'connected', 'message': 'متصل شدید!'})}\n\n"
            
            while True:
                # منتظر پیام بمون
                message = await queue.get()
                yield f"data: {json.dumps(message)}\n\n"
                
        except asyncio.CancelledError:
            pass
        finally:
            # پاکسازی
            if client_id in sse_clients:
                del sse_clients[client_id]
            print(f"🔴 SSE client disconnected: {client_id}")
    
    return StreamingResponse(
        event_generator(),
        media_type="text/event-stream",
        headers={
            "Cache-Control": "no-cache",
            "Connection": "keep-alive",
            "X-Accel-Buffering": "no"
        }
    )

@app.post("/sse/send/{client_id}")
async def sse_send_notification(
    client_id: str,
    title: str = "اعلان جدید",
    message: str = "پیام جدید دارید!",
    current_user: User = Depends(get_current_admin)
):
    """
    ارسال نوتیفیکیشن به یه کاربر خاص
    
    POST /sse/send/user_7?title=سلام&message=پیام جدید داری
    """
    if client_id in sse_clients:
        notification = {
            "type": "notification",
            "title": title,
            "message": message,
            "timestamp": datetime.now().isoformat()
        }
        await sse_clients[client_id].put(notification)
        return {"status": "sent", "client": client_id}
    else:
        return {"status": "offline", "client": client_id}

@app.post("/sse/broadcast")
async def sse_broadcast(
    title: str = "اعلان همگانی",
    message: str = "سلام به همه!",
    current_user: User = Depends(get_current_admin)
):
    """
    ارسال نوتیفیکیشن به همه کاربران
    
    POST /sse/broadcast?title=تخفیف&message=همه محصولات ۵۰٪ تخفیف
    """
    count = 0
    for client_id, queue in sse_clients.items():
        if client_id != "admin":
            notification = {
                "type": "notification",
                "title": title,
                "message": message,
                "timestamp": datetime.now().isoformat()
            }
            await queue.put(notification)
            count += 1
    
    return {"status": "broadcasted", "count": count}

@app.get("/sse/online-clients")
async def get_sse_clients():
    """دریافت لیست کلاینت‌های متصل به SSE"""
    return {
        "clients": list(sse_clients.keys()),
        "count": len(sse_clients)
    }

@app.get("/", tags=["Root"])
def root():
    return {
        "message": "Welcome to Shop API",
        "docs": "/docs",
        "redoc": "/redoc"
    }

# Mount static files (باید آخر کار باشه)
app.mount("/uploads", StaticFiles(directory="uploads"), name="uploads")

def create_admin():
    db = SessionLocal()
    admin = db.query(User).filter(User.username == "admin").first()
    if not admin:
        admin = User(
            username="admin",
            email="admin@gmail.com",
            hashed_password=hash_password("admin123"),
            full_name="Administrator",
            phone="0911111111",
            address="Tehran",
            role=UserRole.ADMIN.value
        )
        db.add(admin)
        db.commit()
        db.refresh(admin)
        print("✅ ادمین پیش‌فرض ساخته شد!")
        print("   Username: admin")
        print("   Password: admin123")
    else:
        print("✅ ادمین از قبل وجود دارد")
    db.close()

# ============= اجرای برنامه =============
if __name__ == "__main__":
    import uvicorn

    if os.path.exists("shop.db"):
        os.remove("shop.db")
        print("✅ دیتابیس قبلی پاک شد")
    
    Base.metadata.create_all(bind=engine)
    print("✅ جداول دیتابیس ساخته شدند")

    create_admin()

    print("🚀 سرور در حال اجرا روی http://127.0.0.1:8000")
    print("📖 مستندات: http://127.0.0.1:8000/docs")
    print("📡 SSE: http://127.0.0.1:8000/sse/subscribe/{client_id}")
    uvicorn.run(app, host="0.0.0.0", port=8000)