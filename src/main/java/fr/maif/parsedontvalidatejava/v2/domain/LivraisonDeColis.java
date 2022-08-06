package fr.maif.parsedontvalidatejava.v2.domain;

import static io.vavr.API.println;

import fr.maif.parsedontvalidatejava.v2.domain.Colis.ColisEnCoursDAcheminement;
import fr.maif.parsedontvalidatejava.v2.domain.Colis.ColisExistant;
import fr.maif.parsedontvalidatejava.v2.domain.Colis.ColisPrisEnCharge;
import fr.maif.parsedontvalidatejava.v2.domain.Colis.ColisRecu;
import fr.maif.parsedontvalidatejava.v2.domain.errors.ColisNonTrouve;
import fr.maif.parsedontvalidatejava.v2.domain.errors.EtatInvalide;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class LivraisonDeColis {

    public final ColisExistants colisExistants;

    public LivraisonDeColis(ColisExistants colisExistants) {
        this.colisExistants = colisExistants;
    }

    public Flux<ColisExistant> listerColis() {
        return this.colisExistants.listerColis();
    }

    public Mono<? extends Colis> gererColis(Colis colis) {
        println("==> LivraisonDeColis : gererColis");
        return switch (colis) {
            case Colis.NouveauColis nouveauColis -> prendreEnChargeLeColis(nouveauColis);
            case ColisExistant colisAGerer -> colisExistants
                    .chercherColisExistantParReference(colisAGerer.reference())
                    .flatMap(colisExistant -> gererColisExistant(colisExistant, colisAGerer))
                    .switchIfEmpty(Mono.error(new ColisNonTrouve(colisAGerer.reference())));
        };
    }

    public Mono<ColisExistant> prendreEnChargeLeColis(Colis.NouveauColis nouveauColis) {
        println("==> LivraisonDeColis : prendreEnChargeLeColis");
        var reference = genererReference();
        var colisPrisEnCharge = nouveauColis.toColisPrisEnCharge(reference);
        return colisExistants.enregistrerColis(colisPrisEnCharge);
    }

    private Colis.ReferenceColis genererReference() {
        String ref = UUID.randomUUID().toString();
        println("==> LivraisonDeColis : genererReference : " + ref);
        return new Colis.ReferenceColis(ref);
    }

    Mono<? extends Colis> gererColisExistant(ColisExistant colisExistant, ColisExistant colisAGerer) {
        println("==> LivraisonDeColis : gererColisExistant");
        return switch (colisExistant) {
            case ColisPrisEnCharge __
                    && colisAGerer instanceof ColisEnCoursDAcheminement colisEnCoursAGerer -> gererColisEnCoursDAcheminement(colisEnCoursAGerer);
            case ColisEnCoursDAcheminement __
                    && colisAGerer instanceof ColisEnCoursDAcheminement colisEnCoursAGerer -> gererColisEnCoursDAcheminement(colisEnCoursAGerer);
            case ColisEnCoursDAcheminement __
                    && colisAGerer instanceof ColisRecu colisEnCoursAGerer -> gererColisRecu(colisEnCoursAGerer);
            case ColisPrisEnCharge __ -> Mono.error(new EtatInvalide("On attend un colis à l'état \"ColisEnCoursDAcheminement\""));
            case ColisEnCoursDAcheminement __ -> Mono.error(new EtatInvalide("On attend un colis à l'état \"ColisEnCoursDAcheminement\" ou \"ColisPrisEnCharge\""));
            case ColisRecu __ -> Mono.error(new EtatInvalide("Le colis est déja reçu"));
        };
    }

    private Mono<ColisExistant> gererColisRecu(ColisRecu colisEnCoursAGerer) {
        return colisExistants.mettreAJourColis(colisEnCoursAGerer);
    }

    private Mono<ColisExistant> gererColisEnCoursDAcheminement(ColisEnCoursDAcheminement colisEnCoursAGerer) {
        return colisExistants.mettreAJourColis(colisEnCoursAGerer);
    }
}
