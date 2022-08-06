package fr.maif.parsedontvalidatejava.v2.domain;

import static io.vavr.API.println;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import fr.maif.parsedontvalidatejava.v2.adapters.serde.ColisJsonFormat;
import io.vavr.API;
import io.vavr.control.Option;
import reactor.core.publisher.Mono;

public class ColisTest {

    private static LivraisonDeColis livraisonDeColis = new LivraisonDeColis(null);

    public static void main(String[] args) {
        shouldCreateNouveauColis();
    }

    //@Test
    static void shouldCreateNouveauColis() {
        var nouveauColis = Colis.NouveauColis.builder()
                .dateDEnvoi(new Colis.DateDEnvoi(LocalDateTime.now()))
                .email(new Colis.Email("georgesginon@gmail.com"))
                .adresse(Colis.Adresse.AdresseBtoC.builder()
                        .ligne1(new Colis.CiviliteNomPrenom("GINON Georges"))
                        .ligne2(Option.none())
                        .ligne3(Option.none())
                        .ligne4(new Colis.NumeroLibelleVoie("48 rue de Brioux"))
                        .ligne5(Option.none())
                        .ligne6(new Colis.CodePostalEtLocaliteOuCedex("79000 NIORT"))
                        .ligne7(Option.none())
                        .build())
                .build();

        println(nouveauColis);

        String write = ColisJsonFormat.nouveauColisFormat().jsonWrite().write(nouveauColis).toPrettyString();

        println("\nNouveau Colis as Json :");
        println(write);

        //assertThat(nouveauColis.email()).isEqualTo(new Colis.Email("georgesginon@gmail.com"));

        var colisPrisEnCharge = nouveauColis.toColisPrisEnCharge(new Colis.ReferenceColis(randomUUID().toString()));

        println();
        println(colisPrisEnCharge);

        String colisPecJson = ColisJsonFormat.colisPrisEnChargeFormat().jsonWrite().write(colisPrisEnCharge).toPrettyString();

        println("\nColis pris en charge as Json :");
        println(colisPecJson);

        println("\nExtract value from Mono :");
        /**livraisonDeColis.gererColisExistant(colisPrisEnCharge, null)
                .doOnNext(API::println)
                .doOnError(API::println)
                .subscribe();*/

        var colisEnCoursDAcheminement = Colis.ColisEnCoursDAcheminement.builder()
                .reference(colisPrisEnCharge.reference())
                .dateDEnvoi(colisPrisEnCharge.dateDEnvoi())
                .email(colisPrisEnCharge.email())
                .position(new Colis.PositionGps(46.316666, -0.466667))
                .adresse(colisPrisEnCharge.adresse())
                .build();

        livraisonDeColis.gererColisExistant(colisPrisEnCharge, colisEnCoursDAcheminement)
                .doOnNext(API::println)
                .doOnError(API::println)
                .subscribe();
    }

}
