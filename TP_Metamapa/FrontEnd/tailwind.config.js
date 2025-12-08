/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/main/resources/**/*.html',
  ],
  theme: {
    extend: {
        colors: {
            'pastel-blue': {
                            '50': '#f0f7ff',
                            '100': '#e0eefd',
                            '200': '#caddfa',
                            '300': '#a6c6f6',
                            '400': '#7daaf0',
                            '500': '#5d8ce9',
                            '600': '#4774d8',
                            '700': '#3a5ebf',
                            '800': '#334e9b',
                            '900': '#2f457a',
                            '950': '#1e2a4a',
                          },
        }
    },
  },
  plugins: [],
}