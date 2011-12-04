package se.faerie.sleep.common.network.tcp

trait NetworkServerAccess {

	def addCommand(command : ServerCommand);
	def stop();
}