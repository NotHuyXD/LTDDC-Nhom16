import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.rentedaccommodation.app',
  appName: 'Rented Accommodation',
  webDir: 'dist',
  server: {
    androidScheme: 'https'
  }
};

export default config;
