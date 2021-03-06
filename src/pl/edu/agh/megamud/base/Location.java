/*******************************************************************************
 * Copyright (c) 2012, AGH
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package pl.edu.agh.megamud.base;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A location in our world. Location has its description, exits and creatures
 * inside. A location can serve a controller own commands (thus it extends
 * CommandCollector).
 */
public class Location extends ItemHolder implements BehaviourHolderInterface {
	private BehaviourHolder behaviourHolder = new BehaviourHolder();
	private Map<String, Location> exits = new HashMap<String, Location>();
	private List<Creature> creatures = new LinkedList<Creature>();
	private String id;
	private String description;
	// private Map<String, List<Command>> map = new HashMap<String,
	// List<Command>>();

	private pl.edu.agh.megamud.dao.Location dbLocation = null;

	public pl.edu.agh.megamud.dao.Location getDbLocation() {
		return dbLocation;
	}

	public void setDbLocation(pl.edu.agh.megamud.dao.Location dbLocation) {
		this.dbLocation = dbLocation;
	}

	public Location(String id, String description, Module module) {
		this.id = id;
		this.description = description;
	}

	public final Map<String, Location> getExits() {
		return exits;
	}

	public final List<Creature> getCreatures() {
		return creatures;
	}
	
	public final Creature getFirstCreature(String name){
		for (Creature c : creatures)
			if(c.getName().equals(name))
				return c;
		return null;
	}

	public String getId() {
		return this.id;
	}

	/**
	 * Use this for location initialisation - add an exit.
	 */
	public final void addExit(String name, Location loc) {
		exits.put(name, loc);
	}

	/**
	 * Executed after a creature entered the room. Notifies other creatures and
	 * sends the "look" command result.
	 */
	public void onAddCreature(Creature creature) {
		creature.controller.write(prepareLook());
		creatures.add(creature);

		for (Creature c : creatures)
			if (c != creature)
				c.controller.onEnter(creature);
	}

	/**
	 * Executed after a creature left this room. Notifies other creatures.
	 */
	public void onRemoveCreature(Creature creature, String usedExit) {
		creatures.remove(creature);
		for (Creature c : creatures)
			if (c != creature)
				c.controller.onLeave(creature, usedExit);
	}

	/**
	 * Executed after a creature "says" something.
	 */
	public void onCreatureSay(Creature creature, String s) {
		for (Creature c : creatures)
			c.controller.onSayInLocation(creature, s);
	}

	public void onItemTransfer(ItemHolder oldOwner, ItemHolder newOwner, Item i) {
		for (Creature c : creatures)
			c.controller.onItemTransfer(oldOwner, newOwner, i);
	}

	public void onItemAppear(Item i, ItemHolder from) {
		for (Creature c : creatures)
			c.controller.onItemTransfer(from, this, i);
	}

	public void onItemDisappear(Item i, ItemHolder to) {
		for (Creature c : creatures)
			c.controller.onItemTransfer(this, to, i);
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Result for command "look". Contains location description, all exists and
	 * other creatures.
	 */
	public final String prepareLook() {
		String desc = "" + getDescription() + "\r\n";
		desc += "Possible exits: ";

		int cnt = exits.size();
		for (String locationPointer : exits.keySet())
			desc += locationPointer + (cnt > 0 ? ", " : "");
		desc += "\r\n";

		for (Item i : items.values())
			desc += "Here is " + i.getName() + " - " + i.getDescription()
					+ "\r\n";
		desc += "\r\n";

		for (Creature creature : creatures) {
			desc += "Here is " + creature.getName() + ", a LV"
					+ creature.getLevel() + " "
					+ creature.getProfession().getName() + ".\r\n";
		}

		return desc;
	}

	@Override
	public List<Behaviour> getBehaviourList() {
		return behaviourHolder.getBehaviourList();
	}

	@Override
	public void setBehaviourList(List<? extends Behaviour> list) {
		behaviourHolder.setBehaviourList(list);
	}

	@Override
	public void addBehaviour(Behaviour behaviour) {
		behaviourHolder.addBehaviour(behaviour);
	}

	@Override
	public void removeBehaviour(Behaviour behaviour) {
		behaviourHolder.removeBehaviour(behaviour);

	}

	@Override
	public List<Behaviour> getBehaviourByType(Class<? extends Behaviour> clazz) {
		return behaviourHolder.getBehaviourByType(clazz);
	}
}
