const mongoose = require("mongoose");

const userScheme = new mongoose.Schema(
  {
    DisplayName: {
      require: true,
      type: String,
      unique: true,
    },
    Email: {
      require: true,
      type: String,
      unique: true,
    },
    Password: {
      required: true,
      type: String,
    },
    Number: {
      required: true,
      type: String,
      unique: true,
    },
    ProfileImage: {
      type: String,
    },
    Token: {
      type: String,
      required: true,
      unique: true,
    },
  },
  {
    timestamps: true,
  }
);

var Users = mongoose.model("User", userScheme);
module.exports = Users;
