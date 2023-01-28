package com.mitocode.serviceImpl;

import com.mitocode.model.Bill;
import com.mitocode.model.BillItem;
import com.mitocode.repo.IBillRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.IBillService;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BillServiceImpl extends CRUDImpl<Bill, Integer> implements IBillService {

    @Autowired
    private IBillRepo repo;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private DatabaseClient client;

    @Override
    protected IGenericRepo<Bill, Integer> getRepo() {
        return repo;
    }

    @Override
    public Mono<Bill> saveTransactional(Bill bill) {
        return Mono.from(connectionFactory.create())
                .map(Connection::createBatch)
                .map(batch -> {
                    batch.add("INSERT INTO bill (id, description, observation, id_client, create_in) VALUES((SELECT NEXTVAL(pg_get_serial_sequence('bill', 'id'))), '" + bill.getDescription() + "','" + bill.getObservation() + "'," + bill.getIdClient() + ",'" + bill.getCreateIn() + "')");
                    for(BillItem item : bill.getItemsList()) {
                        batch.add("INSERT INTO bill_item (id, quantity, id_dish) VALUES ((SELECT CURRVAL('bill_id_seq')), " + item.getQuantity() + ", " + item.getDish().getId() +")");
                        //CURRVAL solo funciona en la sesion, después de un nextval, seguro en multiusuario
                    }
                    return batch;
                })
                //confirmando batch
                .flatMap(batch -> Mono.from(batch.execute()))
                //recuperando id de la última inserción
                .flatMap(x -> client.sql("SELECT last_value FROM bill_id_seq").fetch().one())
                .flatMap(mapa -> {
                    //System.out.println(id.keySet()); //conocer el KEY del map
                    Integer id = Integer.parseInt(String.valueOf(mapa.get("last_value")));
                    bill.setId(id);
                    return Mono.just(bill);
                });
    }
}
