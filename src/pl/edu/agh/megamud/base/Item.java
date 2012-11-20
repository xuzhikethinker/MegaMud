package pl.edu.agh.megamud.base;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pl.edu.agh.megamud.dao.Attribute;
import pl.edu.agh.megamud.dao.CreatureItem;
import pl.edu.agh.megamud.dao.ItemAttribute;
import pl.edu.agh.megamud.dao.PlayerCreature;
import pl.edu.agh.megamud.dao.base.ItemBase;

/**
 * Physical, existing item.
 * @author Tomasz
 *
 */
public class Item implements BehaviourHolderInterface{
	/**
	 * In-database representation of this item.
	 */
	protected CreatureItem creatureItem=null;
	
	/**
	 * In-game owner of this item.
	 */
	protected ItemHolder owner=null;
	
	private Map<Attribute,Long> attributes=new HashMap<Attribute,Long>();
	
	protected String name;
	protected String description;
	
	private BehaviourHolder behaviourHolder = new BehaviourHolder();
	
	public Long getAttributeValue(String x){
		if(attributes.containsKey(x))
			return attributes.get(x);
		return null;
	}
	
	public void setAttribute(String x,Long val){
		for(Iterator<Entry<Attribute,Long>> set=attributes.entrySet().iterator();set.hasNext();){
			Entry<Attribute,Long> next=set.next();
			Attribute a=next.getKey();
			if(a.getName().equals(x)){
				next.setValue(val);
				if(this.creatureItem!=null)
				try{
					ItemAttribute.createDao().deleteBuilder().where().eq("attribute_id",a).and().eq("item_id", this.creatureItem.getItem()).query();
					
					ItemAttribute ne=new ItemAttribute();
					ne.setAttribute(a);
					ne.setItem(this.creatureItem.getItem());
					ne.setLevel(1);
					ne.setValue(val.intValue());
					ItemAttribute.createDao().create(ne);
				}catch(SQLException e){
					e.printStackTrace();
				}
				return;
			}
		}
	}
	
	public Item(CreatureItem it){
		setCreatureItem(it);
	}
	
	public CreatureItem getCreatureItem(){
		return this.creatureItem;
	}
	
	public void setCreatureItem(CreatureItem it){
		this.creatureItem=it;
		
		this.name=it.getItem().getName();
		this.description=it.getItem().getDescription();
		
		List<Attribute> attrs;
		try {
			attrs = Attribute.createDao().queryForAll();
			for(Iterator<Attribute> i=attrs.iterator();i.hasNext();){
				Attribute a=i.next();
				List<ItemAttribute> found=ItemAttribute.createDao().queryBuilder().where().eq("attribute_id",a).and().eq("item_id", it).query();
				if(found.size()>0){
					ItemAttribute first=found.get(0);
					this.attributes.put(a,first.getValue().longValue());
				}else{
					this.attributes.put(a,Long.valueOf(0L));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Item(String name,String description){
		this.name=name;
		this.description=description;
	}
	
	public ItemHolder getOwner() {
		return owner;
	}
	
	public String getName(){
		return name;
	}
	
	public String getDescription(){
		return description;
	}
	
	/**
	 * Use this to move this item to other item holder (creature or location) - especially from/to null, when item is magically (dis)appearing. 
	 * @todo Write this to database.
	 * @param owner
	 * @return True, if given.
	 */	
	public boolean giveTo(ItemHolder newOwner){
		ItemHolder oldOwner=owner;
		
		if(!canBeGivenTo(newOwner)){
			return false;
		}
		
		if(oldOwner!=null){
			oldOwner.removeItem(this);
			oldOwner.onItemDisappear(this,newOwner);
		}
	
		if(creatureItem!=null){
			creatureItem.setCreature(null);
			// @todo
			//creatureItem.setLocation(null);
		}
		
		this.owner=newOwner;
		
		if(newOwner!=null){
			newOwner.addItem(this);
			newOwner.onItemAppear(this,oldOwner);
		}
		
		if(newOwner instanceof Creature){
			if(this.creatureItem==null){
				pl.edu.agh.megamud.dao.Item it;
				try {
					it = pl.edu.agh.megamud.dao.Item.createDao().queryBuilder().where().eq("name", this.name).query().get(0);
				}catch(Exception e){
					it=new pl.edu.agh.megamud.dao.Item();
					it.setName(this.name);
					it.setDescription(this.description);
					try {
						ItemBase.createDao().create(it);
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				try{
					this.creatureItem=new CreatureItem();
					this.creatureItem.setItem(it);
					this.creatureItem.setLevel(1);
					this.creatureItem.setCreature(((Creature)newOwner).getDbCreature());
					
					CreatureItem.createDao().create(this.creatureItem);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else{
				this.creatureItem.setCreature(((Creature)newOwner).getDbCreature());
				try {
					CreatureItem.createDao().update(this.creatureItem);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			this.creatureItem.setCreature(((Creature)newOwner).getDbCreature());
		}else if(newOwner==null){
			try{
				CreatureItem.createDao().delete(this.creatureItem);
			}catch(Exception e){}
			this.creatureItem=null;
		}
		
		return true;
	}	
	
	/**
	 * Internal check, whether this item can be held by a creature. It can depend on its stats (class, some attribute's value) or other held items.
	 * @param owner
	 */
	public boolean canBeGivenTo(ItemHolder owner){
		return true;
	}
	@Override
	public List<Behaviour> getBehaviourList() {
		return behaviourHolder.getBehaviourList();
	}

	@Override
	public void setBehaviourList(List<Behaviour> list) {
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
	public List<Behaviour> getBehaviourByType(Class clazz) {
		return behaviourHolder.getBehaviourByType(clazz);
	}
}
