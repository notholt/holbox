package de.hsrt.holbox.project.powersystem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import de.hsrt.holbox.util.ChangeListener;
import de.hsrt.holbox.util.Log;


/**
 * Manages a list of Parameters.
 * @author notholt
 *
 */

public class ComponentList<T extends PowerSystemComponentSettings> {
	
	protected ChangeListener<PowerSystemComponentSettings> addListener;

	private LinkedHashMap<String, T> components;
	
	/**
	 * Creates an empty parameter list
	 */
	public ComponentList()
	{
		components = new LinkedHashMap<>();
	}
	
	/**
	 * Returns a parameter by its variable name (Id)
	 * @param varname
	 * @return
	 */
	public T getElementById(String varname)
	{
		if (!components.containsKey(varname)) {
			throw new RuntimeException("Error on getting quantity with name " + varname + ": Name does not exist in  system.");
		}
		return this.components.get(varname);
	}
	
	/**
	 * Adds a parameter object to the parameter list. The variable name (varName) is included as the key on the HashMap.
	 * @param param
	 */
	public void add(T add){
		if (components.containsKey(add.getId())) {
			throw new RuntimeException("Error on adding quantity with name " + add.getId() + ": Name already exists in  system.");
		}
		
		this.components.put(add.getId(), add);
		this.added(add);
	}
	
	public int size()
	{
		return this.components.size();
	}
	
	
	public LinkedHashMap<String, T> getMap()
	{
		return this.components;
	}
	
	public void assimilate(T s)
	{
		// TODO: Oh my god
	}

	//@XmlElement
	public List<T> getElementList()
	{
		List<T> list = new ArrayList<>();
        for (var entry : components.entrySet()) {
            list.add(entry.getValue());
        }
        return list;
	}
	
	public void setFromList(List<T> in)
	{
		for(int i=0; i<in.size(); i++)
		{
			System.out.println("Added Component: "+in.get(i).getId());
			this.components.put(in.get(i).getId(), in.get(i));
		}
	}
	
	public void setFromArray(T[] in)
	{
		for(int i=0; i<in.length; i++)
		{
			//System.out.println("Added Component: "+in[i].getId());
			this.components.put(in[i].getId(), in[i]);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void assimilateTo(ComponentList<T> list)
	{
		this.getMap().forEach((key, param)->{
			try {
				list.add((T)param.getAssimilatedObject());
				Log.print(Log.Lvl.INFO, "Set " + param.getClass().getSimpleName() + " with ID: {" + key + "}");
				//System.out.println("[I] Set " + param.getClass().getSimpleName() + " with ID: {" + key + "}");
			}catch(Exception e)
			{
				//e.printStackTrace();
				
				//System.out.println("[E] Error parsing or setting Bus "+ key);
				Log.print(Log.Lvl.ERROR, "[E] Error parsing or setting Bus "+ key);
			}
		});
	}
	
	public void setAddListener(ChangeListener<PowerSystemComponentSettings> listener)
	{
		this.addListener = listener;
	}
	
	public void removeAddListener(ChangeListener<PowerSystemComponentSettings> listener)
	{
		this.addListener = null;
	}
	
	public void added(PowerSystemComponentSettings chg)
	{
		if(addListener!= null)
		{
			this.addListener.changed(chg);
		}
	}
	
}
