const mongoose = require("mongoose");
const express = require("express");
const Users = require("../model/user");

const router = express.Router();
const mongooseUrl =
  "mongodb+srv://rafeek:test12345@cluster0.5emds.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";

router.get("/", (req, res) => {
  console.log("e7na hena");
});

router.get("/:number", (req, res) => {
  mongoose
    .connect(mongooseUrl, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    })
    .then(() => {
      Users.findOne({ Number: req.params.number })
        .then((object) => {
          if (object == null) {
            res.status(404).send();
            return;
          }
          res.status(200).send(JSON.stringify(object));
        })
        .catch((err) => {
          console.log(err);
          res.status(404).send(err);
        });
    })
    .catch((err) => {
      console.log(err);
      res.status(404).send(err);
    });
});

module.exports = router;
