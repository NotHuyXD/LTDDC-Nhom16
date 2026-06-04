const bcrypt = require('bcryptjs');

async function test() {
  const hash = '$2b$12$kIX06lqTubLposdGbV4ShOH4PNwIbL4R9BQGwxDbrsN9uAoB32Htm';
  const match = await bcrypt.compare('password123', hash);
  console.log('Match result for password123:', match);
}

test();
