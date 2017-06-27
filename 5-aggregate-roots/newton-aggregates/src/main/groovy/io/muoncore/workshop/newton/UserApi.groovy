package io.muoncore.workshop.newton

import io.muoncore.Muon
import io.muoncore.newton.eventsource.EventSourceRepository
import io.muoncore.protocol.reactivestream.server.PublisherLookup
import io.muoncore.workshop.newton.user.User
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

import static io.muoncore.protocol.requestresponse.server.HandlerPredicates.path

@Component
class UserApi {

    @Autowired
    UserListView listView

    @Autowired
    EventSourceRepository<User> userRepo

    @Autowired
    Muon muon

    @PostConstruct
    def init() {

        muon.handleRequest(path("/users")) {
          it.ok(listView.users)
        }

        muon.handleRequest(path("/")) { request ->

            String name = request.getRequest().getPayload(Map).name

            User user = userRepo.newInstance {
                new User(name)
            }

            request.ok([
                    "message": "User Created",
                    id: user.id
            ])
        }

        muon.handleRequest(path("/user")) {
            String id = it.request.getPayload(Map).id
            User user = userRepo.load(id)
            it.ok(user)
        }

        muon.handleRequest(path("/user/something")) {
          try {
            String id = it.request.getPayload(Map).id
            String text = it.request.getPayload(Map).text

            User user = userRepo.load(id)
            user.userDidSomething(text)
            userRepo.save(user)

            it.ok(user)
          }catch (Exception e) {
            e.printStackTrace()
          }
        }

        streamEndpoints()
    }

    def streamEndpoints() {

        muon.publishGeneratedSource("/observeupdates", PublisherLookup.PublisherType.HOT_COLD) {
            userRepo.subscribeColdHot(it.args.id)
        }
    }
}
