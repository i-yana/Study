#include "OutputItem.h"
#include "Exeption.h"

using namespace ALU;

bool OutputItem::getValue()
{
	if (ready())
	{
		return m_value;
	}
	if (!m_processed)
	{
		m_processed = true;
		m_value = _input->getValue();
		setReady();
		m_processed = false;
		return m_value;
	}
	else
	{
		throw AluException::Not_Correct_Scheme();
	}

}
bool OutputItem::addInput(pItem input)
{
	_input = input;
	return true;
}
