const path = require('path');
require('dotenv').config({ path: path.join(__dirname, '../.env') });
const { query } = require('../config/db');

async function checkRooms() {
  try {
    const rooms = await query(`
      SELECT id, title,
             (SELECT url FROM room_images ri WHERE ri.room_id = r.id AND ri.is_cover = 1 LIMIT 1) as cover_image
      FROM rooms r LIMIT 5
    `);
    console.log('--- ROOMS LIST COVER IMAGES ---');
    console.log(rooms);

    for (const r of rooms) {
      const images = await query('SELECT url, is_cover FROM room_images WHERE room_id = ?', [r.id]);
      console.log(`Images for Room "${r.title}":`);
      console.log(images);
    }
    process.exit(0);
  } catch (err) {
    console.error('Error:', err);
    process.exit(1);
  }
}

checkRooms();
