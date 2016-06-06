#include "Item.h"

using namespace ALU;

Item::Item()
	: m_ready(false),
	m_value(INITSTATE),
	m_processed(false)
{
	type = LOGIC;
};

Item::~Item(){};

bool Item::ready()
{
	return m_ready;
}
void Item::refreshValue()
{
	m_ready = false;
	m_processed = false;
	m_value = INITSTATE;
}
void Item::setReady()
{
	m_ready = true;
}
