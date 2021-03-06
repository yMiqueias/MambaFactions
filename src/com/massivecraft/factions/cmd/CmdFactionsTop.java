package com.massivecraft.factions.cmd;

import com.massivecraft.massivecore.MassiveException;

public class CmdFactionsTop extends FactionsCommand
{	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsTop() 
	{
		// Aliases
	    this.addAliases("rank", "ranking");
	    
		// Descri��o
		this.setDesc("�6 top �8-�7 Abre o menu do Rank das fac��es.");
		
		/*
		 * A unica raz�o deste comando estar aqui
		 * � porque n�s queriamos mostrar o comando /f top
		 * na lista de comandos do /f ajuda
		 * 
		 * Voc� pode ignorar isso ou apagar se quiser :D
		 */
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException {}
}
