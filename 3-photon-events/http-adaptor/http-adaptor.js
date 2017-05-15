
var Muon = require("muon-core")
var muonurl = process.env.MUON_URL || "amqp://muon:microservices@localhost"

logger.info("Muon is enabled, booting up using url " + muonurl)
var muon = Muon.create("http-adaptor", muonurl);

var http = require('http')
var port = 4545

const requestHandler = (request, response) => {
  if (request.url == "/") {
    var event = {
      "event-type": "RequestReceived",
      "stream-name": "requests",
      "service-id": "http-adaptor",
      payload: {
        headers: request.headers,
        body: request.body
      }
    }

    muon.emit(event).then(function (resp) {
      logger.warn("HTTP Event persisted " + JSON.stringify(resp))
    }).catch(function(error) {
      logger.warn("HTTP Event FAILED " + JSON.stringify(error))
    })
    response.end('Emitted an Event to Photon...')
  } else {
    response.end('Try going to /')
  }
}

const server = http.createServer(requestHandler)

server.listen(port, function(err) {
  if (err) {
    return console.log('something bad happened', err)
  }

  console.log(`server is listening on ${port}`)
})
