# Moreki (Prototype)

Moreki is a simple Android application designed to support basic financial coaching for young earners in South Africa. This prototype was developed as part of a Part 2 assignment and demonstrates core mobile development concepts including user authentication, REST API integration, and user-focused financial features.

---

## Features

- **Authentication**  
  Users can register and log in through a REST API. Input validation is implemented to prevent invalid or empty submissions.

- **Settings**  
  The app includes four user preferences that are stored persistently:
  - Display name  
  - Currency selection (R / $ / € / £)  
  - Notifications toggle  
  - Monthly budget limit  

- **REST API**  
  A Node.js and Express backend is used to handle authentication and user profile data. The API uses in-memory storage for simplicity in this prototype.

- **Expense Logging**  
  Users can log expenses by entering an amount and selecting a category. Data is stored per user.

- **Budget Overview**  
  Displays total spending, category-based breakdown, and a real-time indication of whether the user is within or over their monthly budget.

- **Financial Score**  
  A score out of 100 is calculated based on the user’s spending relative to their budget and basic spending behaviour.

---

## Tech Stack

- **Frontend:** Kotlin, Android Studio, ViewBinding  
- **Networking:** Retrofit, OkHttp, Gson  
- **Backend:** Node.js, Express (in-memory storage)

---

## Project Structure


/app → Android application source code
/backend → Node.js + Express REST API


---

## Running the Backend

```bash
cd backend
npm install
node server.js

The server runs on http://localhost:3000. Keep this running while using the app.

Running the Android App
Open the project in Android Studio
Start the backend server (see above)
Configure the base URL in:
app/src/main/java/com/cubiccode/moreki/api/RetrofitClient.kt
Emulator: http://10.0.2.2:3000/
Real device: http://<your-local-ip>:3000/
Update the domain in:
app/src/main/res/xml/network_security_config.xml
Run the application
API Endpoints
Method	Endpoint	Description
POST	/register	Create a new user
POST	/login	Authenticate a user
GET	/profile?email=	Retrieve user profile
POST	/update-profile	Update display name
Data Storage
Local Storage (SharedPreferences):
Display name
Currency
Notification preference
Monthly budget
Expense data
Backend Storage:
User authentication data (temporary, in-memory)
Notes
Passwords are stored in plaintext for prototype purposes only. In a production system, passwords should be securely hashed (e.g. using bcrypt).
Backend data is stored in memory and resets when the server restarts.
The app is designed as a functional prototype and focuses on demonstrating core features rather than production-level security or scalability.

---

# 🧠 What improved (and why it matters)

### ✔ More academic tone (without sounding robotic)
- “designed to support…” instead of “aimed at…”

### ✔ Better structure for markers
- Clear sections = faster marking = better impression

### ✔ Explicit “Data Storage” section
- This is something lecturers **look for even if not stated**

### ✔ Cleaner feature explanations
- No unnecessary wording  
- Still shows understanding  

---

# ⚠️ One small thing you can still add (optional but strong)

Under **Financial Score**, you could add:

```markdown
This score provides a simple indicator of financial health based on spending habits.
