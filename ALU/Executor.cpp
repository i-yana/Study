#include "Executor.h"
#include "InputItem.h"
#include <memory>
#include <vector>
#include <unordered_map>

namespace ALU
{

	Executor::Executor(const Scheme& scheme)
		:m_scheme(scheme){};

	bitArray Executor::calculate(std::string inputValueOfBits)
	{
		bitArray result(0);
		setInputs(inputValueOfBits);
		for (auto out : m_scheme.outputs)
		{
			result.push_back(out.second->getValue());
		}
		clearInputs();
		return result;
	}

	std::string Executor::showTitleString()
	{
		std::string firstString;
		for (auto item : m_scheme.inputs)
		{
			firstString += item.first + TAB;
		}
		for (auto item : m_scheme.outputs)
		{
			firstString += item.first + TAB;
		}
		return firstString;
	}
	void Executor::setInputs(std::string inputValueOfBits)
	{
		size_t i = 0;
		bool value = false;
		for (auto t : m_scheme.inputs)
		{
			if ((i < inputValueOfBits.size()) && (inputValueOfBits[i] == _TRUE))
			{
				value = true;
			}
			else
			{
				value = false;
			}
			(dynamic_cast<InputItem*>(&(*(t.second)))->setValue(value));
			i++;
		}
	}

	void Executor::clearInputs()
	{
		for (auto item : m_scheme.elements){
			item.second->refreshValue();
		}
	}
}