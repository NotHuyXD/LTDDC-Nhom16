const path = require('path');
require('dotenv').config({ path: path.join(__dirname, '../.env') });
const bcrypt = require('bcryptjs');
const { query } = require('../config/db');
const { v4: uuidv4 } = require('uuid');

const landlordDetails = [
  { name: 'Trần Thị Thu Thảo', email: 'chutro.thao@example.com', phone: '0912345601' },
  { name: 'Phạm Minh Hải', email: 'chutro.hai@example.com', phone: '0912345602' },
  { name: 'Lê Hoàng Nam', email: 'chutro.nam@example.com', phone: '0912345603' },
  { name: 'Nguyễn Bích Phương', email: 'chutro.phuong@example.com', phone: '0912345604' },
  { name: 'Đỗ Tiến Đạt', email: 'chutro.dat@example.com', phone: '0912345605' }
];

const tenantDetails = [
  { name: 'Lê Văn Hùng', email: 'nguoithue.hung@example.com', phone: '0356789001' },
  { name: 'Nguyễn Thị Mai', email: 'nguoithue.mai@example.com', phone: '0356789002' },
  { name: 'Trần Đức Anh', email: 'nguoithue.anh@example.com', phone: '0356789003' },
  { name: 'Hoàng Thanh Trúc', email: 'nguoithue.truc@example.com', phone: '0356789004' },
  { name: 'Vũ Quốc Việt', email: 'nguoithue.viet@example.com', phone: '0356789005' }
];

const roomTemplates = [
  {
    title: 'Phòng trọ dịch vụ cao cấp, đầy đủ nội thất, giờ giấc tự do Bình Thạnh',
    slug: 'phong-tro-dich-vu-cao-cap-gio-giac-tu-do-binh-thanh',
    description: 'Phòng trọ dịch vụ cao cấp vừa mới hoàn thiện, đầy đủ nội thất bao gồm giường nệm, tủ quần áo, tủ lạnh, điều hòa inverter tiết kiệm điện, máy nước nóng lạnh. Khu vực nấu ăn riêng biệt sạch sẽ. Giờ giấc tự do, bảo vệ 24/7, khóa vân tay an toàn tuyệt đối.',
    address: '128/4 Điện Biên Phủ, Phường 25, Quận Bình Thạnh',
    area: 25.0,
    price: 4500000,
    deposit: 5000000,
    imageUrl: 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=800&q=80'
  },
  {
    title: 'Căn hộ dịch vụ Studio full nội thất Quận 3, ngay sau chợ Bàn Cờ',
    slug: 'can-ho-dich-vu-studio-full-noi-that-quan-3',
    description: 'Studio đẳng cấp tọa lạc ngay trung tâm Quận 3. Thiết kế tối giản tinh tế, không gian thoáng mát ngập tràn ánh sáng. Trang bị đầy đủ tivi, tủ lạnh, điều hòa, lò vi sóng, bàn làm việc. Dịch vụ dọn phòng 2 lần/tuần, nước sinh hoạt miễn phí.',
    address: '354 Nguyễn Đình Chiểu, Quận 3',
    area: 28.5,
    price: 5800000,
    deposit: 8000000,
    imageUrl: 'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=800&q=80'
  },
  {
    title: 'Phòng trọ đơn giá tốt, có gác lửng cho sinh viên Gò Vấp',
    slug: 'phong-tro-don-gia-tot-co-gac-lung-go-vap',
    description: 'Phòng trọ diện tích rộng rãi có gác lửng đúc sạch sẽ, toilet riêng trong phòng. Kệ bếp lát đá hoa cương dễ lau chùi. Vị trí đắc địa, gần chợ, siêu thị Lotte và các trường đại học lớn. Internet cáp quang tốc độ cao, có chỗ để xe rộng rãi ở tầng trệt.',
    address: '88/12 Quang Trung, Phường 10, Quận Gò Vấp',
    area: 18.0,
    price: 2500000,
    deposit: 2500000,
    imageUrl: 'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=800&q=80'
  },
  {
    title: 'Căn hộ mini ban công rộng, ngập tràn ánh sáng tự nhiên tại Quận 2',
    slug: 'can-ho-mini-ban-cong-rong-anh-sang-tu-nhien-quan-2',
    description: 'Căn hộ mini cao cấp nằm trong khu biệt thự Thảo Điền yên tĩnh, thoáng mát. Ban công siêu rộng hướng view sông lộng gió, thích hợp trồng hoa hay uống cà phê thư giãn. Đầy đủ trang thiết bị ngoại nhập, giờ giấc tự do không chung chủ.',
    address: '12 Đường Thảo Điền, Thảo Điền, Quận 2',
    area: 32.0,
    price: 7000000,
    deposit: 10000000,
    imageUrl: 'https://images.unsplash.com/photo-1484154218962-a197022b5858?auto=format&fit=crop&w=800&q=80'
  },
  {
    title: 'Phòng trọ an ninh, sạch sẽ tại Quận 7, gần ĐH Tôn Đức Thắng',
    slug: 'phong-tro-an-ninh-sach-se-quan-7-gan-ton-duc-thang',
    description: 'Cho thuê phòng trọ khép kín đầy đủ tiện nghi ngay sát khu đô thị Phú Mỹ Hưng, đi bộ sang ĐH Tôn Đức Thắng và ĐH RMIT chỉ 5 phút. Phòng có cửa sổ thoáng mát, nhà vệ sinh riêng. Camera giám sát 24/24, quản lý nhà thân thiện nhiệt tình.',
    address: '45/8 Lâm Văn Bền, Phường Tân Kiểng, Quận 7',
    area: 22.0,
    price: 3200000,
    deposit: 3500000,
    imageUrl: 'https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=800&q=80'
  },
  {
    title: 'Căn hộ dịch vụ Studio sang trọng tại Phú Nhuận, ra sân bay 5 phút',
    slug: 'can-ho-dich-vu-studio-sang-trong-phu-nhuan',
    description: 'Căn hộ cao cấp đầy đủ tiện nghi tọa lạc trên tuyến đường Phan Xích Long sầm uất. Khu vực tập trung nhiều nhà hàng, cà phê, cửa hàng tiện lợi. Căn hộ có bếp riêng, tủ lạnh side-by-side, máy giặt riêng trong phòng. Phù hợp cho người bận rộn.',
    address: '15 Phan Xích Long, Phường 2, Quận Phú Nhuận',
    area: 30.0,
    price: 6200000,
    deposit: 7000000,
    imageUrl: 'https://images.unsplash.com/photo-1493809842364-78817add7ffb?auto=format&fit=crop&w=800&q=80'
  },
  {
    title: 'Phòng trọ đơn khép kín, không chung chủ Quận 10, gần ĐH Bách Khoa',
    slug: 'phong-tro-don-khep-kin-khong-chung-chu-quan-10',
    description: 'Cho thuê phòng trọ lối đi riêng, không chung chủ, giờ giấc hoàn toàn tự do. Trang bị máy lạnh Daikin đời mới mát sâu, bình nóng lạnh Rossi. Vị trí đắc địa ngay trung tâm Quận 10, đi lại thuận tiện sang Quận 3, Quận 5 và Quận 11.',
    address: '256 Lý Thường Kiệt, Phường 14, Quận 10',
    area: 20.0,
    price: 3500000,
    deposit: 4000000,
    imageUrl: 'https://images.unsplash.com/photo-1505691938895-1758d7feb511?auto=format&fit=crop&w=800&q=80'
  },
  {
    title: 'Căn hộ mini 1 phòng ngủ, thiết kế hiện đại tại Quận 1, full nội thất',
    slug: 'can-ho-mini-1-phong-ngu-hien-dai-quan-1',
    description: 'Căn hộ mini đẳng cấp ngay trung tâm Sài Gòn Hoa Lệ. Gồm 1 phòng khách, 1 phòng ngủ riêng biệt mang lại sự riêng tư. Nội thất gỗ sồi cao cấp cực kỳ sang trọng. Thang máy tốc độ cao, hầm để xe rộng rãi, hệ thống báo cháy tự động tiêu chuẩn.',
    address: '89 Trần Hưng Đạo, Phường Cô Giang, Quận 1',
    area: 40.0,
    price: 8000000,
    deposit: 12000000,
    imageUrl: 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?auto=format&fit=crop&w=800&q=80'
  },
  {
    title: 'Phòng trọ giá rẻ có gác lửng, sạch sẽ thoáng mát tại Tân Bình',
    slug: 'phong-tro-gia-re-co-gac-lung-sach-se-tan-binh',
    description: 'Phòng trọ gác lửng giá sinh viên gần sân bay và các trung tâm thương mại lớn. Toilet khép kín trang bị thiết bị vệ sinh Inax sạch sẽ. Khu dân cư an ninh, yên tĩnh, dân trí cao. Cho nấu ăn thoải mái, giờ giấc tự do đi lại.',
    address: '102/15 Cộng Hòa, Phường 12, Quận Tân Bình',
    area: 19.5,
    price: 2800000,
    deposit: 3000000,
    imageUrl: 'https://images.unsplash.com/photo-1598928506311-c55ded91a20c?auto=format&fit=crop&w=800&q=80'
  },
  {
    title: 'Nhà nguyên căn mini tự quản lý, thích hợp cho nhóm bạn ở Quận Bình Thạnh',
    slug: 'nha-nguyen-can-mini-tu-quan-ly-binh-thanh',
    description: 'Cho thuê căn nhà mini nguyên căn tự quản lý gồm 1 trệt 1 lầu, 2 phòng ngủ sạch sẽ thoáng mát. Thích hợp cho nhóm bạn sinh viên hoặc gia đình trẻ muốn tự do sinh hoạt. Điện nước tính theo đơn giá nhà nước cực kỳ tiết kiệm.',
    address: '15/4 Xô Viết Nghệ Tĩnh, Phường 19, Quận Bình Thạnh',
    area: 45.0,
    price: 9500000,
    deposit: 15000000,
    imageUrl: 'https://images.unsplash.com/photo-1536376072261-38c75010e6c9?auto=format&fit=crop&w=800&q=80'
  }
];

async function seedBulkData() {
  try {
    console.log('🔄 Bắt đầu seed hàng loạt tài khoản & phòng trọ...');

    // 1. Lấy thông tin Ward, RoomType, Amenity có sẵn
    const wards = await query('SELECT id FROM wards');
    const roomTypes = await query('SELECT id FROM room_types');
    const amenities = await query('SELECT id FROM amenities');

    if (wards.length === 0 || roomTypes.length === 0 || amenities.length === 0) {
      console.error('❌ Vui lòng chạy schema.sql và seed_mock_data.js trước để khởi tạo địa giới, loại phòng và tiện nghi.');
      process.exit(1);
    }

    const defaultWardId = wards[0].id;
    const defaultRoomTypeId = roomTypes[0].id;

    // 2. Tạo 5 tài khoản Chủ trọ (Landlord)
    const landlordIds = [];
    const passwordHash = await bcrypt.hash('password123', 12);

    for (const landlord of landlordDetails) {
      const existing = await query('SELECT id FROM users WHERE email = ?', [landlord.email]);
      if (existing.length === 0) {
        const id = uuidv4();
        await query(
          `INSERT INTO users (id, full_name, email, phone, password_hash, role, is_verified, kyc_status)
           VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
          [id, landlord.name, landlord.email, landlord.phone, passwordHash, 'landlord', 1, 'approved']
        );
        landlordIds.push(id);
        console.log(`✅ Đã tạo chủ trọ: ${landlord.name} (${landlord.email})`);
      } else {
        landlordIds.push(existing[0].id);
        console.log(`ℹ️ Chủ trọ đã tồn tại: ${landlord.email}`);
      }
    }

    // 3. Tạo 5 tài khoản Người thuê (Tenant)
    for (const tenant of tenantDetails) {
      const existing = await query('SELECT id FROM users WHERE email = ?', [tenant.email]);
      if (existing.length === 0) {
        const id = uuidv4();
        await query(
          `INSERT INTO users (id, full_name, email, phone, password_hash, role, is_verified, kyc_status)
           VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
          [id, tenant.name, tenant.email, tenant.phone, passwordHash, 'tenant', 1, 'approved']
        );
        console.log(`✅ Đã tạo người thuê: ${tenant.name} (${tenant.email})`);
      } else {
        console.log(`ℹ️ Người thuê đã tồn tại: ${tenant.email}`);
      }
    }

    // 4. Tạo 10 phòng trọ mẫu phân bổ đều cho 5 chủ trọ mới (mỗi chủ trọ 2 phòng)
    for (let i = 0; i < roomTemplates.length; i++) {
      const template = roomTemplates[i];
      const landlordId = landlordIds[i % landlordIds.length]; // Xoay vòng 5 chủ trọ

      const existingRoom = await query('SELECT id FROM rooms WHERE slug = ?', [template.slug]);
      if (existingRoom.length === 0) {
        const roomId = uuidv4();
        
        // Chọn loại phòng tương thích nếu tìm thấy từ khoá
        let roomTypeId = defaultRoomTypeId;
        if (template.title.toLowerCase().includes('căn hộ') || template.title.toLowerCase().includes('studio')) {
          const type = roomTypes.find(r => r && r.name && (r.name.toLowerCase().includes('căn hộ') || r.name.toLowerCase().includes('studio')));
          if (type) roomTypeId = type.id;
        } else {
          const type = roomTypes.find(r => r && r.name && r.name.toLowerCase().includes('phòng trọ'));
          if (type) roomTypeId = type.id;
        }

        // Chọn ngẫu nhiên 1 ward
        const randomWard = wards[Math.floor(Math.random() * wards.length)].id;

        await query(
          `INSERT INTO rooms (id, landlord_id, ward_id, room_type_id, title, slug, description, address, area, price, deposit, max_occupants, allow_pet, allow_cooking, live_with_owner, status)
           VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
          [
            roomId,
            landlordId,
            randomWard,
            roomTypeId,
            template.title,
            template.slug,
            template.description,
            template.address,
            template.area,
            template.price,
            template.deposit,
            Math.floor(Math.random() * 3) + 2, // 2 to 4 occupants
            Math.random() > 0.5 ? 1 : 0, // allow pet
            1, // allow cooking
            0, // live with owner
            'available'
          ]
        );

        // Thêm giá dịch vụ phòng (Điện, Nước, Phí dịch vụ)
        const electricPrice = 3500 + Math.floor(Math.random() * 3) * 500;
        const waterPrice = 15000 + Math.floor(Math.random() * 3) * 5000;
        await query(
          `INSERT INTO room_prices (id, room_id, label, price, unit, is_metered, meter_type) VALUES
           (?, ?, 'Điện', ?, 'kWh', 1, 'electric'),
           (?, ?, 'Nước', ?, 'm³', 1, 'water'),
           (?, ?, 'Internet & Rác', 100000, 'tháng', 0, NULL)`,
          [uuidv4(), roomId, electricPrice, uuidv4(), roomId, waterPrice, uuidv4(), roomId]
        );

        // Thêm ảnh phòng
        await query(
          `INSERT INTO room_images (id, room_id, url, is_cover, sort_order) VALUES
           (?, ?, ?, 1, 0)`,
          [uuidv4(), roomId, template.imageUrl]
        );

        // Thêm tiện nghi phòng (ngẫu nhiên 4-6 tiện nghi từ database)
        const shuffled = amenities.sort(() => 0.5 - Math.random());
        const selected = shuffled.slice(0, Math.floor(Math.random() * 3) + 4);
        for (const amen of selected) {
          await query('INSERT INTO room_amenities (room_id, amenity_id) VALUES (?, ?)', [roomId, amen.id]);
        }

        console.log(`✅ Đã tạo phòng: "${template.title}" cho chủ trọ`);
      } else {
        console.log(`ℹ️ Phòng trọ đã tồn tại: "${template.title}"`);
      }
    }

    console.log('🎉 Hoàn thành seed 5 chủ trọ, 5 người thuê và 10 phòng trọ!');
    process.exit(0);
  } catch (error) {
    console.error('❌ Lỗi khi seed dữ liệu:', error);
    process.exit(1);
  }
}

seedBulkData();
