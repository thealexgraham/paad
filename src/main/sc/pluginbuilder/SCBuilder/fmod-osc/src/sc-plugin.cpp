/*==============================================================================
Gain DSP Plugin Example
Copyright (c), Firelight Technologies Pty, Ltd 2004-2014.

This example shows how to create a simple gain DSP effect.
==============================================================================*/

#ifdef WIN32
    #define _CRT_SECURE_NO_WARNINGS
#endif

#include <math.h>
#include <stdio.h>
#include <assert.h>
#include <windows.h>
#include <iostream>
#include <string>

#include "../ip/UdpSocket.h"
#include "../osc/OscOutboundPacketStream.h"

#include "../inc/fmod.hpp"

#define ADDRESS "127.0.0.1"

///=============================
///%%%CONST_DEFINES%%%

#define TOP_ROUTE "/Live-pingdemolive"
static const int PORT = 53120;

///=============================

#define OUTPUT_BUFFER_SIZE 1024

#ifdef WIN32
    #define _CRT_SECURE_NO_WARNINGS
#endif

/*
	First thing the plugin does when it loads is sends a message to supercollider with a random ID
	SC then creates the instruments and sets listeners with this random ID
	Now that version of the plugin can operate independently of the other ones

*/

extern "C" {
    F_DECLSPEC F_DLLEXPORT FMOD_DSP_DESCRIPTION* F_STDCALL FMODGetDSPDescription();
}

enum
{
	///=============================
	///%%%PARAM_ENUMS%%%
	
	FMOD_OTHER_PARAM_MASTER_MASTERGAIN,

	
	FMOD_OTHER_PARAM_RANDOMPASS10_PERCENT,

	
	FMOD_OTHER_PARAM_RANDOMPASS9_PERCENT,

	
	FMOD_OTHER_PARAM_RANDOMPASS6_PERCENT,

	
	FMOD_OTHER_PARAM_STEADYACTION12_TIME,

	///=============================
    FMOD_OTHER_NUM_PARAMETERS
};

FMOD_RESULT F_CALLBACK FMOD_Other_dspcreate       (FMOD_DSP_STATE *dsp_state);
FMOD_RESULT F_CALLBACK FMOD_Other_dsprelease      (FMOD_DSP_STATE *dsp_state);
FMOD_RESULT F_CALLBACK FMOD_Other_dspreset        (FMOD_DSP_STATE *dsp_state);
FMOD_RESULT F_CALLBACK FMOD_Other_dspread         (FMOD_DSP_STATE *dsp_state, float *inbuffer, float *outbuffer, unsigned int length, int inchannels, int *outchannels);
FMOD_RESULT F_CALLBACK FMOD_Other_dspsetparamfloat(FMOD_DSP_STATE *dsp_state, int index, float value);
FMOD_RESULT F_CALLBACK FMOD_Other_dspsetparamint  (FMOD_DSP_STATE *dsp_state, int index, int value);
FMOD_RESULT F_CALLBACK FMOD_Other_dspsetparambool (FMOD_DSP_STATE *dsp_state, int index, bool value);
FMOD_RESULT F_CALLBACK FMOD_Other_dspsetparamdata (FMOD_DSP_STATE *dsp_state, int index, void *data, unsigned int length);
FMOD_RESULT F_CALLBACK FMOD_Other_dspgetparamfloat(FMOD_DSP_STATE *dsp_state, int index, float *value, char *valuestr);
FMOD_RESULT F_CALLBACK FMOD_Other_dspgetparamint  (FMOD_DSP_STATE *dsp_state, int index, int *value, char *valuestr);
FMOD_RESULT F_CALLBACK FMOD_Other_dspgetparambool (FMOD_DSP_STATE *dsp_state, int index, bool *value, char *valuestr);
FMOD_RESULT F_CALLBACK FMOD_Other_dspgetparamdata (FMOD_DSP_STATE *dsp_state, int index, void **value, unsigned int *length, char *valuestr);

/// ==========================
///%%%STATIC_PARAM_DESC%%%
	
static FMOD_DSP_PARAMETER_DESC p_Master_masterGain; // p_Master_masterGain

	
static FMOD_DSP_PARAMETER_DESC p_RandomPass10_percent; // p_RandomPass10_percent

	
static FMOD_DSP_PARAMETER_DESC p_RandomPass9_percent; // p_RandomPass9_percent

	
static FMOD_DSP_PARAMETER_DESC p_RandomPass6_percent; // p_RandomPass6_percent

	
static FMOD_DSP_PARAMETER_DESC p_SteadyAction12_time; // p_SteadyAction12_time

/// ===========================

FMOD_DSP_PARAMETER_DESC *FMOD_Other_dspparam[FMOD_OTHER_NUM_PARAMETERS] =
{
	/// =====================================
	///%%%PARAM_DESC_POINTERS%%%
	
	&p_Master_masterGain, 

	
	&p_RandomPass10_percent, 

	
	&p_RandomPass9_percent, 

	
	&p_RandomPass6_percent, 

	
	&p_SteadyAction12_time, 

	/// =====================================
};

FMOD_DSP_DESCRIPTION FMOD_Other_Desc =
{
    FMOD_PLUGIN_SDK_VERSION,
	/// =====================================
	///%%%DESC_NAME%%%
	
    "PAAD Live-pingdemolive",

	/// =====================================
    0x00010000,     // plug-in version
    1,              // number of input buffers to process
    1,              // number of output buffers to process
    FMOD_Other_dspcreate,
    FMOD_Other_dsprelease,
    FMOD_Other_dspreset,
    FMOD_Other_dspread,
    0,
    0,
    FMOD_OTHER_NUM_PARAMETERS,
    FMOD_Other_dspparam,
    FMOD_Other_dspsetparamfloat,
    0, // FMOD_Other_dspsetparamint,
    0, //FMOD_Other_dspsetparambool,
    0, // FMOD_Other_dspsetparamdata,
    FMOD_Other_dspgetparamfloat,
    0, // FMOD_Other_dspgetparamint,
    0, //FMOD_Other_dspgetparambool,
    0, // FMOD_Other_dspgetparamdata,
    0,
    0
};

extern "C"
{

F_DECLSPEC F_DLLEXPORT FMOD_DSP_DESCRIPTION* F_STDCALL FMODGetDSPDescription()
{
	/// ====================================
	///%%%PARAM_DESCRIPTIONS%%%
	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_Master_masterGain, "masterGain", "f", "Adjusts masterGain", 0, 1, 0.88);

	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_RandomPass10_percent, "percent", "f", "Adjusts percent", 0, 100, 57);

	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_RandomPass9_percent, "percent", "f", "Adjusts percent", 0, 100, 56.66);

	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_RandomPass6_percent, "percent", "f", "Adjusts percent", 0, 100, 54.99);

	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_SteadyAction12_time, "time", "f", "Adjusts time", 0, 5, 0.18);

	/// ====================================

    return &FMOD_Other_Desc;
}

}

class FMODOtherState
{
public:
    FMODOtherState();

    void process(float *inbuffer, float *outbuffer, unsigned int length, int channels);
    void reset();

	/// ================================
	///%%%PARAM_CLASS_DECS%%%
	
	void setMaster_masterGain(float); 
	float Master_masterGain() const { return m_Master_masterGain; }  

	
	void setRandomPass10_percent(float); 
	float RandomPass10_percent() const { return m_RandomPass10_percent; }  

	
	void setRandomPass9_percent(float); 
	float RandomPass9_percent() const { return m_RandomPass9_percent; }  

	
	void setRandomPass6_percent(float); 
	float RandomPass6_percent() const { return m_RandomPass6_percent; }  

	
	void setSteadyAction12_time(float); 
	float SteadyAction12_time() const { return m_SteadyAction12_time; }  

	/// ================================

	void sendParam(const char *, const char *, const char *, float);
	void sendMsg(const char *, float);
	void setOSCID(int);

	int osc_id() const { return m_osc_id; }

private:

	/// =============================
	///%%%PARAM_PRIVATE_DECS%%%
	
	float m_Master_masterGain; 

	
	float m_RandomPass10_percent; 

	
	float m_RandomPass9_percent; 

	
	float m_RandomPass6_percent; 

	
	float m_SteadyAction12_time; 

	/// ===============================

	int m_osc_id;
	bool was_set;
};

FMODOtherState::FMODOtherState()
{
    reset();
}

void FMODOtherState::setOSCID(int theID) {

	if (!was_set) {
		m_osc_id = theID;
		sendMsg("/start", m_osc_id);
		was_set = true;
	}
}

void FMODOtherState::process(float *inbuffer, float *outbuffer, unsigned int length, int channels)
{
	sendMsg("/process", 1);
}

void FMODOtherState::reset()
{

}

/// =======================================
///%%%PARAM_CLASS_FUNCTIONS%%%

void FMODOtherState::setMaster_masterGain(float value) { 
	m_Master_masterGain = value;
}


void FMODOtherState::setRandomPass10_percent(float value) { 
	m_RandomPass10_percent = value;
}


void FMODOtherState::setRandomPass9_percent(float value) { 
	m_RandomPass9_percent = value;
}


void FMODOtherState::setRandomPass6_percent(float value) { 
	m_RandomPass6_percent = value;
}


void FMODOtherState::setSteadyAction12_time(float value) { 
	m_SteadyAction12_time = value;
}

/// =======================================

void FMODOtherState::sendParam(const char *addr, const char *param_name, const char *instance_id, float value) {

	UdpTransmitSocket transmitSocket( IpEndpointName( ADDRESS, PORT ) );
    
    char buffer[OUTPUT_BUFFER_SIZE];
    osc::OutboundPacketStream p( buffer, OUTPUT_BUFFER_SIZE );
    
    p << osc::BeginBundleImmediate
        << osc::BeginMessage( addr )
			<< "instance"
			<< param_name
			<< instance_id
            << value << osc::EndMessage
        << osc::EndBundle;
    
    transmitSocket.Send( p.Data(), p.Size() );
}

void FMODOtherState::sendMsg(const char *addr, float value) {

	//std::string address = TOP_ROUTE;
	//address += addr;

	UdpTransmitSocket transmitSocket( IpEndpointName( ADDRESS, PORT ) );
    
    char buffer[OUTPUT_BUFFER_SIZE];
    osc::OutboundPacketStream p( buffer, OUTPUT_BUFFER_SIZE );
    
    p << osc::BeginBundleImmediate
        << osc::BeginMessage( addr )
            << value << osc::EndMessage
        << osc::EndBundle;
    
    transmitSocket.Send( p.Data(), p.Size() );
}

FMOD_RESULT F_CALLBACK FMOD_Other_dspcreate(FMOD_DSP_STATE *dsp_state)
{
    dsp_state->plugindata = (FMODOtherState *)FMOD_DSP_STATE_MEMALLOC(dsp_state, sizeof(FMODOtherState), FMOD_MEMORY_NORMAL, "FMODOtherState");

	FMODOtherState *state = (FMODOtherState *)dsp_state->plugindata;
	state->sendMsg("/live/start", 1);

    if (!dsp_state->plugindata)
    {
        return FMOD_ERR_MEMORY;
    }
    return FMOD_OK;
}

FMOD_RESULT F_CALLBACK FMOD_Other_dsprelease(FMOD_DSP_STATE *dsp_state)
{
    FMODOtherState *state = (FMODOtherState *)dsp_state->plugindata;
	state->sendMsg("/live/stop", 1);
	//state->sendMsg("/dying", state->osc_id());
    FMOD_DSP_STATE_MEMFREE(dsp_state, state, FMOD_MEMORY_NORMAL, "FMODOtherState");
    return FMOD_OK;
}

FMOD_RESULT F_CALLBACK FMOD_Other_dspread(FMOD_DSP_STATE *dsp_state, float *inbuffer, float *outbuffer, unsigned int length, int inchannels, int * /*outchannels*/)
{
    FMODOtherState *state = (FMODOtherState *)dsp_state->plugindata;
    state->process(inbuffer, outbuffer, length, inchannels); // input and output channels count match for this effect
    return FMOD_OK;
}

FMOD_RESULT F_CALLBACK FMOD_Other_dspreset(FMOD_DSP_STATE *dsp_state)
{
    FMODOtherState *state = (FMODOtherState *)dsp_state->plugindata;
    state->reset();
    return FMOD_OK;
}

FMOD_RESULT F_CALLBACK FMOD_Other_dspsetparamfloat(FMOD_DSP_STATE *dsp_state, int index, float value)
{
    FMODOtherState *state = (FMODOtherState *)dsp_state->plugindata;

    switch (index)
    {
	/// ==============================================
	///%%%SET_PARAM_FLOAT%%%
	
	case FMOD_OTHER_PARAM_MASTER_MASTERGAIN:
		state->setMaster_masterGain(value);
		state->sendParam("/module/live/paramc", "masterGain", "1", value);
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_RANDOMPASS10_PERCENT:
		state->setRandomPass10_percent(value);
		state->sendParam("/module/live/paramc", "percent", "8", value);
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_RANDOMPASS9_PERCENT:
		state->setRandomPass9_percent(value);
		state->sendParam("/module/live/paramc", "percent", "7", value);
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_RANDOMPASS6_PERCENT:
		state->setRandomPass6_percent(value);
		state->sendParam("/module/live/paramc", "percent", "4", value);
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_STEADYACTION12_TIME:
		state->setSteadyAction12_time(value);
		state->sendParam("/module/live/paramc", "time", "10", value);
		return FMOD_OK;

	/// =============================================
	}
    return FMOD_ERR_INVALID_PARAM;
}

FMOD_RESULT F_CALLBACK FMOD_Other_dspgetparamfloat(FMOD_DSP_STATE *dsp_state, int index, float *value, char *valuestr)
{
    FMODOtherState *state = (FMODOtherState *)dsp_state->plugindata;

    switch (index)
    {
	/// ===============================================
	///%%%GET_PARAM_FLOAT%%%
	
	case FMOD_OTHER_PARAM_MASTER_MASTERGAIN:
		*value = state->Master_masterGain();
		if (valuestr) sprintf(valuestr, "% fl", state->Master_masterGain());
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_RANDOMPASS10_PERCENT:
		*value = state->RandomPass10_percent();
		if (valuestr) sprintf(valuestr, "% fl", state->RandomPass10_percent());
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_RANDOMPASS9_PERCENT:
		*value = state->RandomPass9_percent();
		if (valuestr) sprintf(valuestr, "% fl", state->RandomPass9_percent());
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_RANDOMPASS6_PERCENT:
		*value = state->RandomPass6_percent();
		if (valuestr) sprintf(valuestr, "% fl", state->RandomPass6_percent());
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_STEADYACTION12_TIME:
		*value = state->SteadyAction12_time();
		if (valuestr) sprintf(valuestr, "% fl", state->SteadyAction12_time());
		return FMOD_OK;

	/// ==============================================
	}

    return FMOD_ERR_INVALID_PARAM;
}
