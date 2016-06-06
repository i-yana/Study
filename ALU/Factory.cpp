#include "Factory.h"
#include "Exeption.h"

using namespace ALU;

pItem ItemFactory::indicateItem(std::string type)
{
	if (type == _INPUT)
	{
		return std::shared_ptr<InputItem>(new InputItem());
	}
	if (type == _OUTPUT)
	{
		return std::shared_ptr<OutputItem>(new OutputItem());
	}
	if (type == _MULTIPLEXER)
	{
		return std::shared_ptr<MultiPlexer>(new MultiPlexer());
	}
	if (type == _AND)
	{
		return std::shared_ptr<LogicItem>(new LogicItem(AND_FUNC));
	}
	if (type == _OR)
	{
		return std::shared_ptr<LogicItem>(new LogicItem(OR_FUNC));
	}
	if (type == _XOR)
	{
		return std::shared_ptr<LogicItem>(new LogicItem(XOR_FUNC));
	}
	throw AluException::Factory_exeption(type);
}
