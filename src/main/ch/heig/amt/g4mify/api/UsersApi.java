package ch.heig.amt.g4mify.api;

import ch.heig.amt.g4mify.model.Badge;
import ch.heig.amt.g4mify.model.Domain;
import ch.heig.amt.g4mify.model.User;
import ch.heig.amt.g4mify.model.view.badge.BadgeSummary;
import ch.heig.amt.g4mify.model.view.badgeType.BadgeTypeSummary;
import ch.heig.amt.g4mify.model.view.user.UserDetail;
import ch.heig.amt.g4mify.model.view.user.UserSummary;
import ch.heig.amt.g4mify.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.heig.amt.g4mify.model.view.ViewUtils.*;

/**
 * @author ldavid
 * @created 11/14/16
 */
@RestController
@RequestMapping("/api/users")
public class UsersApi extends AbstractDomainApi {

    @Autowired
    private UsersRepository usersRepository;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserSummary>> index(@RequestParam(required = false, defaultValue = "0") long page,
                                                   @RequestParam(required = false, defaultValue = "50") long pageSize) {

        Domain domain = getDomain();
        List<UserSummary> users = usersRepository.findByDomain(domain)
                .skip(page * pageSize)
                .limit(pageSize)
                .map(outputView(UserSummary.class)::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody UserSummary body) {

        Domain domain = getDomain();
        User input = inputView(User.class).from(body);
        input.setDomain(domain);

        Optional<User> userOpt = usersRepository.findByDomainAndProfileId(domain, body.profileId);

        if (userOpt.isPresent()) {
            throw new ApiException("ProfileID '" + body.profileId + "' is already in use'");
        } else {
            User user = usersRepository.save(input);
            URI uri = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(user.getId())
                    .toUri();

            return ResponseEntity.created(uri).body(outputView(UserSummary.class).from(user));
        }
    }

    @RequestMapping("/{pid}")
    public ResponseEntity<UserSummary> show(@PathVariable String pid) {
        return usersRepository.findByProfileId(pid)
                .filter(this::canAccess)
                .map(outputView(UserSummary.class)::from)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.status(HttpStatus.NOT_FOUND)::build);
    }

    @RequestMapping(path = "/{pid}", method = RequestMethod.PUT)
    public ResponseEntity<UserSummary> update(@PathVariable String pid,
                                             @RequestBody UserDetail body) {

        return usersRepository.findByProfileId(pid)
                .filter(this::canAccess)
                .map(user -> {
                    updateView(user).with(body);
                    return usersRepository.save(user);
                })
                .map(outputView(UserSummary.class)::from)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.status(HttpStatus.NOT_FOUND)::build);
    }

    @RequestMapping(path = "/{pid}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable String pid) {
        return usersRepository.findByProfileId(pid)
                .filter(this::canAccess)
                .map(user -> {
                    usersRepository.delete(user);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(ResponseEntity.status(HttpStatus.NOT_FOUND)::build);
    }

    @RequestMapping(path = "/{pid}/badges", method = RequestMethod.GET)
    public ResponseEntity<List<BadgeSummary>> index(@PathVariable String pid,
                                                    @RequestParam(required = false, defaultValue = "0") long page,
                                                    @RequestParam(required = false, defaultValue = "50") long pageSize) {
        return usersRepository.findByProfileId(pid)
                .filter(this::canAccess)
                .map(user -> user.getBadges()
                        .stream()
                        .skip(page * pageSize)
                        .limit(pageSize)
                        .map(outputView(BadgeSummary.class).map("type", viewMap(BadgeTypeSummary.class))::from)
                        .collect(Collectors.toList())
                )
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.status(HttpStatus.NOT_FOUND)::build);
    }

    private boolean canAccess(User user) {
        return user.getDomain().getId() == getDomain().getId();
    }
}
