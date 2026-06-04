const path = require('path');
require('dotenv').config({ path: path.join(__dirname, '../.env') });
const { query } = require('../config/db');

async function checkUsers() {
  try {
    const users = await query('SELECT id, full_name, email, role, password_hash, is_verified FROM users');
    console.log('--- DANH SÁCH USER TRONG DATABASE ---');
    console.log(JSON.stringify(users, null, 2));
    process.exit(0);
  } catch (err) {
    console.error('Lỗi khi truy vấn user:', err);
    process.exit(1);
  }
}

checkUsers();
