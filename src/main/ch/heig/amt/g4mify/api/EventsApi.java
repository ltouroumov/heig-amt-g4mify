package ch.heig.amt.g4mify.api;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import ch.heig.amt.g4mify.extension.SpringExtension;
import ch.heig.amt.g4mify.model.Event;
import ch.heig.amt.g4mify.repository.EventsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author ldavid
 * @created 11/21/16
 */
@RestController
@RequestMapping("/{domainId}/events")
public class EventsApi extends BaseDomainApi {

    @Autowired
    private EventsRepository eventsRepository;

    @Autowired
    private ActorSystem system;

    @Autowired
    private SpringExtension ext;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> post(@RequestBody Event input) {
        Event event = eventsRepository.save(input);
        ActorRef actor = system.actorOf(ext.props("eventActor"));
        actor.tell(event, null);

        return ResponseEntity.ok(event);
    }

}
