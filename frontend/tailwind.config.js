/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      fontFamily: {
        courgette: ['Courgette', 'cursive'],
        notosansjp: ['Noto Sans JP', 'sans-serif'],
      },
      backgroundColor: {
        'custom-green': '#9DC183',
        'custom-black' : '#131313',
        'custom-green2' : '#a9ba9d',
        'custom-green3': '#ebffeb', 
        'white': '#ffffff', 
        'grey': '#808080', 
        'light-gray': '#D3D3D3'
      },
      textColor: {
        'custom-green': '#9DC183',
        'custom-black' : '#131313',
        'custom-green2' : '#a9ba9d',
        'custom-green3': '#131313', 
        'white': '#ffffff', 
        'grey': '#808080', 
        'light-gray': '#D3D3D3',
        'custom-white': '#F2F3F4'
      },
      borderColor: {
        'white': '#ffffff', 
        'grey': '#808080', 
        'light-gray': '#D3D3D3'
      },
    },
  },
  plugins: [],
};
