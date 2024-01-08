object model {
    operator fun invoke(componentsAndActions: ModelScope.() -> Unit) {
        ModelScope().componentsAndActions()
    }
    
    val settings: MutableMap<String, Any> = mutableMapOf()
    val units: MutableList<Item> = mutableListOf()
}

class ComponentsScope {
    fun unit(
        name: String,
        unitSettings: Item.() -> Unit
    ) {
        println("unit: $name")
        val unit = Item(name)
        model.units.add(unit)
        unit.unitSettings()
    }
}

public class possibleValues(vararg fromValues: Any) {
    val see = mutableSetOf<Any>()
    init {
        fromValues.forEach { value ->
            see.add(value)
        }
    }
    fun printValues() {
        for (v in see) {
            println("key: value: $v")
        }
    }
}

class Item(
    val name: String
) {
    val values = mutableMapOf<String, Any>()
    val possibleValues = mutableMapOf<String, possibleValues>()
    
    fun itemValueByName(name: String): Any? {
        return values[name]
    }
    fun printValues() {
        println("%%%")
        for ((k, v) in values) {
            println("$$$ key: $k = value: $v")
            val p = possibleValues[k]
            p!!.see.forEach { v ->
                println("key: value: $v")
            }
        }
    }
    
    lateinit var nameToBind: String
    lateinit var valueToBind: Any
    
    fun bind(
        name: String,
    ): Item {
        nameToBind = name
        println("Item: $name:")
        return this
    }
    
    infix fun Item.with(value: Any): Item {
        println("\tIn ${this.name}: $nameToBind = $value")
        valueToBind = value
        this.possibleValues.put(nameToBind, possibleValues(value))
        this.values.put(nameToBind, value)
        return this
    }
    
    infix fun Item.from(values: possibleValues): Item {
        if (values.see.contains(valueToBind)) {
            this.possibleValues.put(nameToBind, values)
        } else {
            throw IllegalAccessException("*_* BLA")
        }
        
        return this
    }
}

class ActionsScope {
    fun functionalAction(
        name: String,
        functionalActionsBlock: FunctionalActionScope.() -> Unit
    ) {
        println("FA: $name:")
        FunctionalActionScope().functionalActionsBlock()
    }
}

class ModelScope {
    fun components(
        ComponentsBlock: ComponentsScope.() -> Unit
    ) {
        println("_ComponentsScope_")
        ComponentsScope().ComponentsBlock()
    }
    
    fun actions(
        functionalActionsBlock: ActionsScope.() -> Unit
    ) {
        println("_ActionsScope_")
        ActionsScope().functionalActionsBlock()
    }
        
    fun settings(
        settingsBlock: SettingsScope.() -> Unit
    ) {
        println("_SettingsScope_")
        SettingsScope().settingsBlock()
    }
}

class SettingsScope {
}

class FunctionalActionScope {
    fun activity(
        name: String
    ): ActivityScope {
        println("A: $name")
        return ActivityScope(name)
    }
    
    infix fun ActivityScope.under(
        underFunction: ActivityScope.() -> Unit
    ): ActivityScope {
        println("UNDER ActivityScope name: ${this.name}")
        this.underFunction()
        return this
    }

    infix fun ActivityScope.run(
        runFunction: ActivityScope.() -> Unit
    ): ActivityScope {
        println("RUN ActivityScope name: ${this.name}")
        this.runFunction()
        return this
    }
}

class ActivityScope(
    val name: String
) {
    fun ActivityScope.item(name: String): ActivityScope {
        println("ActivityScope.item: ${name}")
        return this
    }
    
    infix fun ActivityScope.has(
        hasFunction: Value.() -> Value
    ): ActivityScope {
        println("ActivityScope.has")
        Value(inItem = this).hasFunction()
        return this
    }
    
    
}

class Value(
	var name: String = "",
    val inItem: ActivityScope? = null
) {
    fun Value.value(name: String): Value {
        println("ActivityScope.value: ${name}")
        this.name = name
        return this
    }
    
    infix fun Value.eq(value: Any): Value {
//         println("Value.eq: ${this.name} == ${value} is ${model.units[this.inItem.name]!!.itemValueByName(this.name) == value}")
        return this
    }
    infix fun Value.ne(value: Any): Value {
        println("Value.ne: ${this.name} != ${value} is ${this.name != value}")
        return this
    }
    
}




fun main() {
    for (unitt in model.units) {
        if (unitt.name == "Processor") {
//         	println(model.units[unitt].values["Processor"].itemValueByName("Data"))
        }
    }

    
    
    model {
        settings {
   
        }
        components {
            unit("Processor") {
                bind("Data") with "empty" from possibleValues("empty", "full")
                bind("ProcessingState") with "no" from possibleValues("no", "yes")
            }
        }
        actions {
            functionalAction("FA1") {
                activity("A1") under { 
                    item("Processor") has {
                        value("Data") eq "empry"
                        value("Data") ne "full"
                        value("ProcessingState") 
                    }  
                } run {
                    
                }
            }
        }
    }
    
    for (unitt in model.units) {
        println(unitt.name)
        unitt.printValues()
    }

}
