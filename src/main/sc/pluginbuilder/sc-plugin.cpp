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

#define TOP_ROUTE "/Live-liveinttest"
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
	
	FMOD_OTHER_PARAM_MASTER_AMP,

	
	FMOD_OTHER_PARAM_FM2OP4_FREQ,

	
	FMOD_OTHER_PARAM_FM2OP4_INDEX,

	FMOD_OTHER_PARAM_TEST_INT,

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
	
static FMOD_DSP_PARAMETER_DESC p_Master_amp; // p_Master_amp

	
static FMOD_DSP_PARAMETER_DESC p_FM2OP4_freq; // p_FM2OP4_freq

	
static FMOD_DSP_PARAMETER_DESC p_FM2OP4_index; // p_FM2OP4_index

static FMOD_DSP_PARAMETER_DESC p_test_int; // p_FM2OP4_index
/// ===========================

FMOD_DSP_PARAMETER_DESC *FMOD_Other_dspparam[FMOD_OTHER_NUM_PARAMETERS] =
{
	/// =====================================
	///%%%PARAM_DESC_POINTERS%%%
	
	&p_Master_amp, 

	
	&p_FM2OP4_freq, 

	
	&p_FM2OP4_index,
	
	&p_test_int,

	/// =====================================
};

FMOD_DSP_DESCRIPTION FMOD_Other_Desc =
{
    FMOD_PLUGIN_SDK_VERSION,
	/// =====================================
	///%%%DESC_NAME%%%
	
    "SuperCollider Live-liveinttest",

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
    FMOD_Other_dspsetparamint,
    0, //FMOD_Other_dspsetparambool,
    0, // FMOD_Other_dspsetparamdata,
    FMOD_Other_dspgetparamfloat,
    FMOD_Other_dspgetparamint,
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
	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_Master_amp, "amp", "f", "Adjusts amp", 0, 1, 0.5);

	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_FM2OP4_freq, "freq", "f", "Adjusts freq", 100, 1000, 200);

	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_FM2OP4_index, "index", "f", "Adjusts index", 0, 1000, 100);

	FMOD_DSP_INIT_PARAMDESC_INT(p_test_int, "maxval", "f", "Adjusts index", 0, 9, 10, false, NULL);

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
	
	void setMaster_amp(float); 
	float Master_amp() const { return m_Master_amp; }  

	
	void setFM2OP4_freq(float); 
	float FM2OP4_freq() const { return m_FM2OP4_freq; }  

	
	void setFM2OP4_index(float); 
	float FM2OP4_index() const { return m_test_int; }  

	void setTest_int(int); 
	float test_int() const { return m_test_int; }  

	/// ================================

	void sendParam(const char *, const char *, const char *, float);
	void sendParamInt(const char *, const char *, const char *, int);
	void sendMsg(const char *, float);
	void setOSCID(int);

	int osc_id() const { return m_osc_id; }

private:

	/// =============================
	///%%%PARAM_PRIVATE_DECS%%%
	
	float m_Master_amp; 

	
	float m_FM2OP4_freq; 

	
	float m_FM2OP4_index;

	int m_test_int;

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

void FMODOtherState::setMaster_amp(float value) { 
	m_Master_amp = value;
}


void FMODOtherState::setFM2OP4_freq(float value) { 
	m_FM2OP4_freq = value;
}


void FMODOtherState::setFM2OP4_index(float value) { 
	m_FM2OP4_index = value;
}

void FMODOtherState::setTest_int(int value) { 
	m_test_int = value;
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

void FMODOtherState::sendParamInt(const char *addr, const char *param_name, const char *instance_id, int value) {

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
	
	case FMOD_OTHER_PARAM_MASTER_AMP:
		state->setMaster_amp(value);
		state->sendParam("/module/live/paramc", "amp", "1", value);
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_FM2OP4_FREQ:
		state->setFM2OP4_freq(value);
		state->sendParam("/module/live/paramc", "freq", "4", value);
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_FM2OP4_INDEX:
		state->setFM2OP4_index(value);
		state->sendParam("/module/live/paramc", "index", "4", value);
		return FMOD_OK;

	/// =============================================
	}
    return FMOD_ERR_INVALID_PARAM;
}

FMOD_RESULT F_CALLBACK FMOD_Other_dspsetparamint(FMOD_DSP_STATE *dsp_state, int index, int value)
{
    FMODOtherState *state = (FMODOtherState *)dsp_state->plugindata;

    switch (index)
    {
	/// ==============================================
	///%%%SET_PARAM_FLOAT%%%
	
	case FMOD_OTHER_PARAM_TEST_INT:
		state->setTest_int(value);
		state->sendParamInt("/module/live/paramc", "lengthMax", "5", value);
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
	
	case FMOD_OTHER_PARAM_MASTER_AMP:
		*value = state->Master_amp();
		if (valuestr) sprintf(valuestr, "% fl", state->Master_amp());
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_FM2OP4_FREQ:
		*value = state->FM2OP4_freq();
		if (valuestr) sprintf(valuestr, "% fl", state->FM2OP4_freq());
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_FM2OP4_INDEX:
		*value = state->FM2OP4_index();
		if (valuestr) sprintf(valuestr, "% fl", state->FM2OP4_index());
		return FMOD_OK;

	/// ==============================================
	}

    return FMOD_ERR_INVALID_PARAM;
}

FMOD_RESULT F_CALLBACK FMOD_Other_dspgetparamint(FMOD_DSP_STATE *dsp_state, int index, int *value, char *valuestr)
{
    FMODOtherState *state = (FMODOtherState *)dsp_state->plugindata;

    switch (index)
    {
	/// ===============================================
	///%%%GET_PARAM_FLOAT%%%
	
	case FMOD_OTHER_PARAM_TEST_INT:
		*value = state->test_int();
		return FMOD_OK;

	/// ==============================================
	}

    return FMOD_ERR_INVALID_PARAM;
}
