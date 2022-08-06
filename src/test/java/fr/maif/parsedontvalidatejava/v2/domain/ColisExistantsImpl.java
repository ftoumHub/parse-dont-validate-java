package fr.maif.parsedontvalidatejava.v2.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ColisExistantsImpl implements ColisExistants{

    @Override
    public Mono<Colis.ColisExistant> chercherColisExistantParReference(Colis.ReferenceColis referenceColis) {
        return null;
    }

    @Override
    public Mono<Colis.ColisExistant> enregistrerColis(Colis.ColisPrisEnCharge colisPrisEnCharge) {
        return null;
    }

    @Override
    public Mono<Colis.ColisExistant> mettreAJourColis(Colis.ColisExistant colisExistant) {
        return null;
    }

    @Override
    public Flux<Colis.ColisExistant> listerColis() {
        return null;
    }
}
