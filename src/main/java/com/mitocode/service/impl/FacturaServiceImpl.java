package com.mitocode.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.mitocode.model.Factura;
import com.mitocode.model.FacturaItem;
import com.mitocode.repo.IFacturaRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.IFacturaService;

import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Mono;

@Service
public class FacturaServiceImpl extends CRUDImpl<Factura, Integer> implements IFacturaService {

	@Autowired
	private IFacturaRepo repo;
	
	@Autowired
	private DatabaseClient client;
	
	@Autowired
	private ConnectionFactory connectionFactory;

	@Override
	protected IGenericRepo<Factura, Integer> getRepo() {
		return repo;
	}

	@Override
	public Mono<Factura> registrarTransaccional(Factura factura) {
				
		return Mono.from(connectionFactory.create())			
			.map(connection -> connection.createBatch())
			.map(batch -> {
				batch.add("INSERT INTO Factura (id, descripcion, observacion, id_cliente, creado_en) VALUES((SELECT NEXTVAL(pg_get_serial_sequence('factura', 'id'))), '" + factura.getDescripcion() + "','" + factura.getObservacion() + "'," + factura.getIdCliente() + ",'" + factura.getCreadoEn() + "')");
				for(FacturaItem item : factura.getItemsList()) {
					batch.add("INSERT INTO factura_item (id_factura, cantidad, id_plato) VALUES ((SELECT CURRVAL('factura_id_seq')), " + item.getCantidad() + ", " + item.getPlato().getId() +")");
					//CURRVAL solo funciona en la sesion, después de un nextval, seguro en multiusuario
				}					
	            return batch;
	        })			
			//confirmando batch
			.flatMap(batch -> Mono.from(batch.execute()))
			//recuperando id de la última inserción
			.flatMap(x -> client.execute("SELECT last_value FROM factura_id_seq").fetch().one())
			.flatMap(mapa -> {				
				//System.out.println(id.keySet()); //conocer el KEY del map
				Integer id = Integer.parseInt(String.valueOf(mapa.get("last_value")));
				factura.setId(id);
				return Mono.just(factura);
			});
		
		//String sqlFactura = "INSERT INTO Factura (descripcion, observacion, id_cliente, creado_en) VALUES(:descripcion, :observacion, :idCliente, :creadoEn)";
		//String sqlFacturaItem = null;

		//Flux<FacturaItem> items = Flux.fromIterable(factura.getItemsList());
		
		/*return client.execute(sqlFactura)
			.bind("descripcion", factura.getDescripcion())
			.bind("observacion", factura.getObservacion())
			.bind("idCliente", factura.getIdCliente())
			.bind("creadoEn", LocalDateTime.now())			
			.fetch().rowsUpdated()
			.then(items)
			.then(Mono.just(factura));
			/*.then(
					client.execute(sqlFacturaItem)
					.bind(index, value)
					.then()
			);*/		

	}

}
