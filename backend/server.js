const express = require('express');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

// In-memory storage — no database required for this prototype
let users = [];

// Helper to find a user by email
function findUser(email) {
    return users.find(u => u.email === email);
}

// POST /register
app.post('/register', (req, res) => {
    const { name, email, password } = req.body;

    if (!name || !email || !password) {
        return res.status(400).json({ success: false, message: 'Name, email and password are required' });
    }

    if (findUser(email)) {
        return res.status(409).json({ success: false, message: 'Email already registered' });
    }

    const newUser = { name, email, password, preferences: {} };
    users.push(newUser);

    console.log(`Registered: ${email}`);
    res.status(201).json({ success: true, message: 'User registered successfully' });
});

// POST /login
app.post('/login', (req, res) => {
    const { email, password } = req.body;
    const user = findUser(email);

    if (!user || user.password !== password) {
        return res.status(401).json({ success: false, message: 'Invalid email or password' });
    }

    console.log(`Login: ${email}`);
    res.status(200).json({ success: true, message: 'Login successful', name: user.name, email: user.email });
});

// GET /profile?email=...
app.get('/profile', (req, res) => {
    const { email } = req.query;
    const user = findUser(email);

    if (!user) {
        return res.status(404).json({ success: false, message: 'User not found' });
    }

    res.status(200).json({ success: true, name: user.name, email: user.email, preferences: user.preferences });
});

// POST /update-profile
app.post('/update-profile', (req, res) => {
    const { email, name } = req.body;
    const user = findUser(email);

    if (!user) {
        return res.status(404).json({ success: false, message: 'User not found' });
    }

    if (name) user.name = name;

    console.log(`Profile updated: ${email}`);
    res.status(200).json({ success: true, message: 'Profile updated', name: user.name });
});

const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Moreki backend running on http://localhost:${PORT}`);
});