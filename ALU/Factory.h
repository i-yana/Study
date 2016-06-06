#ifndef FACTORY_H
#define FACTORY_H

#include "LogicItem.h"
#include "InputItem.h"
#include "OutputItem.h"
#include "Multiplexer.h"

#include <string>
#include <functional>

namespace ALU 
{
	const std::string ALU_MARK = "ALU";
	const std::string ID_MARK = "id";
	const std::string TYPE = "type";
	const std::string OP_MARK = "Op";
    const std::string _INPUT = "in";
    const std::string _OUTPUT = "out";
	const std::string INPUTS = "inputs";
	const std::string OUTPUTS = "outputs";
    const std::string _MULTIPLEXER = "multiplexer";
    const std::string _AND = "and";
    const std::string _OR = "or";
    const std::string _XOR = "xor";
    
    const std::function<bool(bool,bool )> OR_FUNC = [](bool a, bool b) -> bool { return a||b; };
    const std::function<bool(bool,bool )> AND_FUNC = [](bool a, bool b) -> bool { return a&&b; };
	const std::function<bool(bool, bool)> XOR_FUNC = [](bool a, bool b) -> bool { return (a || b) && (!a || !b); };
    
    class ItemFactory 
	{
    public:
        ItemFactory(){};
		pItem indicateItem(std::string type);
    };
}

#endif
