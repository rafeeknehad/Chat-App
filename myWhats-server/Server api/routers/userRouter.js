const { request } = require("express");
const express = require("express");
const mongoose = require("mongoose");
const Users = require("../model/user");

const router = express.Router();
const mongooseUrl =
  "mongodb+srv://rafeek:test12345@cluster0.5emds.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";

router.get("/", (req, res) => {
  console.log("e7na hena");
});

router.get("/:email/:password", (req, res) => {
  console.log(req.params.email);
  console.log(req.params.password);
  mongoose
    .connect(mongooseUrl, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    })
    .then(() => {
      console.log(`successfully connected`);
      Users.findOne({ Email: req.params.email })
        .then((result) => {
          console.log("e7na hena");
          if (result == null) {
            res.status(404).end();
          } else {
            res.status(200).end(JSON.stringify(result));
          }
        })
        .catch((err) => {
          console.log(err);
        });
    })
    .catch((err) => {
      console.log(err);
    });
});

router.get("/:number", (req, res) => {
  mongoose
    .connect(mongooseUrl, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    })
    .then(() => {
      Users.findOne({ Number: req.params.number })
        .then((result) => {
          if (result == null) {
            res.status(404).end();
          } else {
            console.log(result);
            res.status(200).end(JSON.stringify(result));
          }
        })
        .catch((err) => {
          console.log(err);
        });
    })
    .catch((err) => {
      console.log(err);
    });
});

router.post("/", (req, res) => {
  mongoose
    .connect(mongooseUrl, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    })
    .then(() => {
      console.log(`successfully connected`);
      console.log(req.body);
      var newUser = Users({
        DisplayName: req.body.DisplayName,
        Email: req.body.Email,
        Password: req.body.Password,
        Number: req.body.Number,
        Token: req.body.Token,
      });
      newUser
        .save()
        .then((result) => {
          console.log(result);
          res.status(200).end(JSON.stringify(result));
        })
        .catch((err) => {
          res.status(404).end("error message");
          console.log(err);
        });
    })
    .catch((err) => {
      console.log(err);
    });
});

router.put("/:id", (req, res) => {
  const option = { new: true };
  console.log("e7na hena " + req.params.id + " " + req.body);
  const id = req.params.id;
  const body = req.body;
  mongoose
    .connect(mongooseUrl, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    })
    .then(() => {
      Users.findByIdAndUpdate(
        id,
        {
          $set: {
            DisplayName: body.DisplayName,
            Email: body.Email,
            Password: body.password,
            Number: body.Number,
            ProfileImage: body.ProfileImage,
          },
        },
        {
          new: true,
        }
      )
        .then((newObject) => {
          console.log(newObject);
          res.status(404).send(JSON.stringify(newObject));
        })
        .catch((err) => {
          console.log(err);
          res.status(404).send();
        });
    })
    .catch((err) => {
      console.log(err);
    });
});

router.delete("/", (req, res) => {});

module.exports = router;
