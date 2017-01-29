package com.artemis.compile

import kotlin.comparisons.compareBy

private fun List<Symbol>.lookup(owner: Class<*>,
                                node: Node) : Symbol {

    return this.first { it.owner == owner && it.field == node.field }
}


fun asSymbols(owner: Class<*>? = null,
              node: Node,
              symbols: List<Symbol>,
              accumulator: MutableSet<Symbol> = mutableSetOf()): MutableSet<Symbol> {

    owner?.let { accumulator.add(symbols.lookup(it, node)) }

    if (!isBuiltInType(node.type))
        node.children.forEach { asSymbols(node.type, it, symbols, accumulator) }

    return accumulator
}

fun usedSymbolsOf(entities: List<EntityData>, allSymbols: List<Symbol>) : List<Symbol> {
    return mutableSetOf<Symbol>()
            .into(input = entities, xf = nodesToSymbols(allSymbols))
            .sortedWith(compareBy({ it.owner.simpleName }, Symbol::field))
}

