
var Muon = require("muon-core")
var muonurl = process.env.MUON_URL || "amqp://muon:microservices@localhost"

logger.info("Muon is enabled, booting up using url " + muonurl)

var muon = Muon.create("hello-world-node", muonurl);

muon.handle('/', function (request, respond) {
    console.dir(request)
    respond({
        message: "Hi there!"
    })
})
