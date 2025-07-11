require('dotenv').config();
console.log('[CONFIG] SENHA_AMAZON =', process.env.SENHA_AMAZON);

const { defineConfig } = require("cypress");

module.exports = defineConfig({
  e2e: {
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },

    env: {
      SENHA_AMAZON: process.env.SENHA_AMAZON
    }
  },
});
