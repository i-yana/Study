#include "LogicItem.h"
#include "Exeption.h"

using namespace ALU;

LogicItem::LogicItem(std::function<bool(bool, bool)> operation)
	: Item(), a_in(nullptr), b_in(nullptr)
{
	type = LOGIC;
	this->_op = operation;
}
bool LogicItem::addInput(pItem input)
{
	if (!a_in)
	{
		a_in = input;
		return true;
	}
	if (!b_in)
	{
		b_in = input;
		return true;
	}
	return false;
}
bool LogicItem::getValue()
{
	if (ready())
	{
		return m_value;
	}
	if (m_processed)
	{
		throw AluException::Not_Correct_Scheme();
	}
	if ((a_in != nullptr) && (b_in != nullptr))
	{
		m_value = _op(a_in->getValue(), b_in->getValue());
	}
	else
	{
		throw AluException::Not_input_Item_state();
	}
	m_processed = false;
	setReady();
	return m_value;
}
