const express = require("express");
const bodyPasrser = require("body-parser");
const userRouter = require("./routers/userRouter");
const contactRouter = require("./routers/contactRouter");

const app = express();
app.use(bodyPasrser.json());
const port = 3000;

app.use("/user", userRouter);
app.use("/contact", contactRouter);
const server = require("http").createServer(app);
server.listen(port, () => {
  console.log(`Server running on port ${port}`);
});
