#include "Multiplexer.h"
#include "Exeption.h"

using namespace ALU;

bool MultiPlexer::getValue()
{
	if (ready())
	{
		return m_value;
	}
	if (m_processed)
	{
		throw AluException::Not_Correct_Scheme();
	}
	int number = 0;
	if (inputs.size() > 8)
	{
		number *= 2;
		number += (inputs[8]->getValue() ? 1 : 0);
	}
	if (inputs.size() > 9)
	{
		number *= 2;
		number += (inputs[9]->getValue() ? 1 : 0);
	}
	if (inputs.size() > 10)
	{
		number *= 2;
		number += (inputs[10]->getValue() ? 1 : 0);
	}
	if (inputs[number] != nullptr)
	{
		m_processed = true;
		m_value = inputs[number]->getValue();
		m_processed = false;
		setReady();

		return m_value;
	}
	else 
	{
		return INITSTATE;
	}
}
bool MultiPlexer::addInput(pItem input)
{
	inputs[dataInputs % 8] = input;
	dataInputs++;
	return true;
}
void MultiPlexer::addControlInput(pItem input)
{
	inputs.push_back(input);
}
