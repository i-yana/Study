#include "InputItem.h"

using namespace ALU;

void InputItem::setValue(bool newValue)
{
	m_value = newValue;
	setReady();
}
bool InputItem::getValue()
{
	if (m_ready)
	{
		return m_value;
	}
	else
	{
		return INITSTATE;
	}
}
bool InputItem::addInput(pItem input)
{
	return true;
}
