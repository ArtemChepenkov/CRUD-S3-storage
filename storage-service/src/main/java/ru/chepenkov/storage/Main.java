package ru.chepenkov.storage;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import ru.chepenkov.storage.DBMigration.DatabaseMigration;
import ru.chepenkov.storage.gRPC.Server.ServiceGrpcServer;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		DatabaseMigration.runMigrations(
				"jdbc:postgresql://postgres:5432/storage",
				"postgres",
				"password"
				);
		Server server = ServerBuilder
				.forPort(9090)
				.addService(new ServiceGrpcServer())
				.build();
		System.out.println("gRPC сервер запущен на порту 9090");
		server.start();
		server.awaitTermination();
	}

}
