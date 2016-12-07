package ch.heig.amt.g4mify.repository;

import ch.heig.amt.g4mify.model.Counter;
import ch.heig.amt.g4mify.model.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * @author ldavid
 * @created 12/7/16
 */
@Component
public interface CountersRepository extends JpaRepository<Counter, Long> {

    public Stream<Counter> findByDomain(Domain domain);

}
