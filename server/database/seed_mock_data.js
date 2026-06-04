const path = require('path');
require('dotenv').config({ path: path.join(__dirname, '../.env') });
const bcrypt = require('bcryptjs');
const { query } = require('../config/db');
const { v4: uuidv4 } = require('uuid');

async function seedMockData() {
  try {
    console.log('🔄 Bắt đầu seed dữ liệu mẫu cho phòng trọ...');

    // 1. Tạo Địa giới hành chính mẫu (TP. Hồ Chí Minh -> Quận 1 -> Phường Bến Nghé)
    const provinceId = uuidv4();
    const provinceCode = '79';
    await query(
      'INSERT IGNORE INTO provinces (id, name, code) VALUES (?, ?, ?)',
      [provinceId, 'Thành phố Hồ Chí Minh', provinceCode]
    );
    const provRows = await query('SELECT id FROM provinces WHERE code = ?', [provinceCode]);
    const finalProvId = provRows[0].id;

    const districtId = uuidv4();
    const districtCode = '760';
    await query(
      'INSERT IGNORE INTO districts (id, province_id, name, code) VALUES (?, ?, ?, ?)',
      [districtId, finalProvId, 'Quận 1', districtCode]
    );
    const distRows = await query('SELECT id FROM districts WHERE code = ?', [districtCode]);
    const finalDistId = distRows[0].id;

    const wardId = uuidv4();
    const wardCode = '26734';
    await query(
      'INSERT IGNORE INTO wards (id, district_id, name, code) VALUES (?, ?, ?, ?)',
      [wardId, finalDistId, 'Phường Bến Nghé', wardCode]
    );
    const wardRows = await query('SELECT id FROM wards WHERE code = ?', [wardCode]);
    const finalWardId = wardRows[0].id;
    console.log('✅ Đã khởi tạo địa giới hành chính mẫu (TP.HCM, Quận 1, Phường Bến Nghé)');

    // 2. Tạo Tài khoản chủ trọ mẫu (Landlord)
    const landlordEmail = 'landlord@example.com';
    const existingLandlord = await query('SELECT id FROM users WHERE email = ?', [landlordEmail]);
    let landlordId;

    const passwordHash = await bcrypt.hash('password123', 12);
    if (existingLandlord.length === 0) {
      landlordId = uuidv4();
      await query(
        `INSERT INTO users (id, full_name, email, phone, password_hash, role, is_verified, kyc_status)
         VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
        [landlordId, 'Nguyễn Văn Chủ Nhà', landlordEmail, '0987654321', passwordHash, 'landlord', 1, 'approved']
      );
      console.log(`✅ Đã tạo tài khoản chủ trọ mẫu: ${landlordEmail} / password123`);
    } else {
      landlordId = existingLandlord[0].id;
      await query(
        `UPDATE users SET password_hash = ?, role = 'landlord', is_verified = 1 WHERE id = ?`,
        [passwordHash, landlordId]
      );
      console.log(`✅ Đã cập nhật mật khẩu và vai trò cho tài khoản chủ trọ mẫu: ${landlordEmail} / password123`);
    }

    // 3. Lấy Room Types và Amenities hiện tại từ database
    const roomTypes = await query('SELECT id, name FROM room_types');
    const amenities = await query('SELECT id, name FROM amenities');

    if (roomTypes.length === 0 || amenities.length === 0) {
      console.error('❌ Lỗi: Vui lòng chạy schema.sql trước để tạo Room Types và Amenities.');
      return;
    }

    // Lấy một vài loại phòng phổ biến
    const singleRoomType = roomTypes.find(r => r.name.includes('Phòng trọ đơn')) || roomTypes[0];
    const miniApartmentType = roomTypes.find(r => r.name.includes('Căn hộ mini')) || roomTypes[0];

    // 4. Tạo phòng trọ mẫu 1: Căn Hộ Mini Hạng Sang
    const room1Id = uuidv4();
    const room1Title = 'Căn hộ mini Studio đẳng cấp Q1 - Đầy đủ tiện nghi ban công rộng';
    const room1Slug = 'can-ho-mini-studio-dang-cap-q1-ban-cong';
    
    // Check if room exists
    const existingRoom1 = await query('SELECT id FROM rooms WHERE slug = ?', [room1Slug]);
    if (existingRoom1.length === 0) {
      await query(
        `INSERT INTO rooms (id, landlord_id, ward_id, room_type_id, title, slug, description, address, area, price, deposit, max_occupants, allow_pet, allow_cooking, live_with_owner, status)
         VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
        [
          room1Id,
          landlordId,
          finalWardId,
          miniApartmentType.id,
          room1Title,
          room1Slug,
          'Căn hộ Studio cực kỳ hiện đại ngay trung tâm Quận 1. Đầy đủ tiện nghi cao cấp như máy lạnh, tủ lạnh, bếp riêng, tủ quần áo lớn, ban công đón gió cực thoáng mát. Thích hợp cho chuyên gia nước ngoài hoặc người đi làm văn phòng thuê dài hạn. Bảo vệ 24/7, giờ giấc tự do không chung chủ.',
          'Số 12 Lê Duẩn',
          35.5,
          6500000,
          10000000,
          2,
          1, // allow pet
          1, // allow cooking
          0, // live with owner
          'available'
        ]
      );

      // Thêm giá dịch vụ phòng 1
      await query(
        `INSERT INTO room_prices (id, room_id, label, price, unit, is_metered, meter_type) VALUES
         (?, ?, 'Điện', 4000, 'kWh', 1, 'electric'),
         (?, ?, 'Nước', 20000, 'm³', 1, 'water'),
         (?, ?, 'Phí dịch vụ + Internet', 150000, 'tháng', 0, NULL)`,
        [uuidv4(), room1Id, uuidv4(), room1Id, uuidv4(), room1Id]
      );

      // Thêm ảnh phòng 1
      await query(
        `INSERT INTO room_images (id, room_id, url, is_cover, sort_order) VALUES
         (?, ?, 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?auto=format&fit=crop&w=800&q=80', 1, 0),
         (?, ?, 'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=800&q=80', 0, 1)`,
        [uuidv4(), room1Id, uuidv4(), room1Id]
      );

      // Thêm tiện nghi phòng 1 (Wifi, Điều hòa, Máy giặt, Tủ lạnh, Bếp, Giường, Tủ quần áo)
      const selectedAmenities1 = amenities.filter(a => 
        ['WiFi', 'Điều hòa', 'Máy giặt', 'Tủ lạnh', 'Nóng lạnh', 'Bếp', 'Giường', 'Tủ quần áo'].includes(a.name)
      );
      for (const amen of selectedAmenities1) {
        await query('INSERT INTO room_amenities (room_id, amenity_id) VALUES (?, ?)', [room1Id, amen.id]);
      }
      console.log('✅ Đã tạo phòng mẫu 1: Căn hộ mini Studio Q1');
    }

    // 5. Tạo phòng trọ mẫu 2: Phòng trọ đơn giá rẻ cho sinh viên
    const room2Id = uuidv4();
    const room2Title = 'Phòng trọ đơn giá rẻ trung tâm Quận 1 cho học sinh sinh viên';
    const room2Slug = 'phong-tro-don-gia-re-trung-tam-q1-sinh-vien';

    const existingRoom2 = await query('SELECT id FROM rooms WHERE slug = ?', [room2Slug]);
    if (existingRoom2.length === 0) {
      await query(
        `INSERT INTO rooms (id, landlord_id, ward_id, room_type_id, title, slug, description, address, area, price, deposit, max_occupants, allow_pet, allow_cooking, live_with_owner, status)
         VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
        [
          room2Id,
          landlordId,
          finalWardId,
          singleRoomType.id,
          room2Title,
          room2Slug,
          'Phòng trọ tiện nghi, sạch sẽ, giá siêu hạt dẻ ngay trung tâm Quận 1, cách các trường đại học lớn chỉ 5-10 phút đi xe. Phòng có gác lửng, kệ bếp nấu ăn, nhà vệ sinh riêng. Có sẵn tủ quần áo và quạt mát, bảo vệ trực 24/24 vô cùng an ninh.',
          'Số 45 Nguyễn Thị Minh Khai',
          18.0,
          2800000,
          3000000,
          2,
          0, // no pet
          1, // allow cooking
          0, // no live with owner
          'available'
        ]
      );

      // Thêm giá dịch vụ phòng 2
      await query(
        `INSERT INTO room_prices (id, room_id, label, price, unit, is_metered, meter_type) VALUES
         (?, ?, 'Điện', 3500, 'kWh', 1, 'electric'),
         (?, ?, 'Nước', 100000, 'người/tháng', 0, NULL),
         (?, ?, 'Internet WiFi', 50000, 'người/tháng', 0, NULL)`,
        [uuidv4(), room2Id, uuidv4(), room2Id, uuidv4(), room2Id]
      );

      // Thêm ảnh phòng 2
      await query(
        `INSERT INTO room_images (id, room_id, url, is_cover, sort_order) VALUES
         (?, ?, 'https://images.unsplash.com/photo-1598928506311-c55ded91a20c?auto=format&fit=crop&w=800&q=80', 1, 0),
         (?, ?, 'https://images.unsplash.com/photo-1536376072261-38c75010e6c9?auto=format&fit=crop&w=800&q=80', 0, 1)`,
        [uuidv4(), room2Id, uuidv4(), room2Id]
      );

      // Thêm tiện nghi phòng 2 (Wifi, Máy giặt chung, Bãi xe, Bếp, Giường)
      const selectedAmenities2 = amenities.filter(a => 
        ['WiFi', 'Máy giặt', 'Bãi xe', 'Bếp', 'Giường'].includes(a.name)
      );
      for (const amen of selectedAmenities2) {
        await query('INSERT INTO room_amenities (room_id, amenity_id) VALUES (?, ?)', [room2Id, amen.id]);
      }
      console.log('✅ Đã tạo phòng mẫu 2: Phòng trọ đơn Quận 1');
    }

    console.log('🎉 Seed dữ liệu thành công! Ứng dụng của bạn đã có dữ liệu mẫu cực kỳ đẹp mắt.');
    process.exit(0);
  } catch (error) {
    console.error('❌ Lỗi khi seed dữ liệu:', error);
    process.exit(1);
  }
}

seedMockData();
