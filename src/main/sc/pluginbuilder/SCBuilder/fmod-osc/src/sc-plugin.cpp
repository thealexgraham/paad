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

#define TOP_ROUTE "/Live-liveparmtest"
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

	
	FMOD_OTHER_PARAM_PANFM3OP2_RATIO1,

	
	FMOD_OTHER_PARAM_PANFM3OP2_INDEX2,

	
	FMOD_OTHER_PARAM_PANFM3OP2_CAR_FREQ,

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

	
static FMOD_DSP_PARAMETER_DESC p_PanFM3OP2_ratio1; // p_PanFM3OP2_ratio1

	
static FMOD_DSP_PARAMETER_DESC p_PanFM3OP2_index2; // p_PanFM3OP2_index2

	
static FMOD_DSP_PARAMETER_DESC p_PanFM3OP2_car_freq; // p_PanFM3OP2_car_freq

/// ===========================

FMOD_DSP_PARAMETER_DESC *FMOD_Other_dspparam[FMOD_OTHER_NUM_PARAMETERS] =
{
	/// =====================================
	///%%%PARAM_DESC_POINTERS%%%
	
	&p_Master_amp, 

	
	&p_PanFM3OP2_ratio1, 

	
	&p_PanFM3OP2_index2, 

	
	&p_PanFM3OP2_car_freq, 

	/// =====================================
};

FMOD_DSP_DESCRIPTION FMOD_Other_Desc =
{
    FMOD_PLUGIN_SDK_VERSION,
	/// =====================================
	///%%%DESC_NAME%%%
	
    "SuperCollider Live-liveparmtest",

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
	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_Master_amp, "amp", "f", "Adjusts amp", 0, 1, 0.32);

	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_PanFM3OP2_ratio1, "ratio1", "f", "Adjusts ratio1", 0, 5, 2.84);

	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_PanFM3OP2_index2, "index2", "f", "Adjusts index2", 0, 1000, 145);

	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_PanFM3OP2_car_freq, "car_freq", "f", "Adjusts car_freq", 100, 1000, 510);

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

	
	void setPanFM3OP2_ratio1(float); 
	float PanFM3OP2_ratio1() const { return m_PanFM3OP2_ratio1; }  

	
	void setPanFM3OP2_index2(float); 
	float PanFM3OP2_index2() const { return m_PanFM3OP2_index2; }  

	
	void setPanFM3OP2_car_freq(float); 
	float PanFM3OP2_car_freq() const { return m_PanFM3OP2_car_freq; }  

	/// ================================

	void sendParam(const char *, const char *, const char *, float);
	void sendMsg(const char *, float);
	void setOSCID(int);

	int osc_id() const { return m_osc_id; }

private:

	/// =============================
	///%%%PARAM_PRIVATE_DECS%%%
	
	float m_Master_amp; 

	
	float m_PanFM3OP2_ratio1; 

	
	float m_PanFM3OP2_index2; 

	
	float m_PanFM3OP2_car_freq; 

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


void FMODOtherState::setPanFM3OP2_ratio1(float value) { 
	m_PanFM3OP2_ratio1 = value;
}


void FMODOtherState::setPanFM3OP2_index2(float value) { 
	m_PanFM3OP2_index2 = value;
}


void FMODOtherState::setPanFM3OP2_car_freq(float value) { 
	m_PanFM3OP2_car_freq = value;
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
	
	case FMOD_OTHER_PARAM_MASTER_AMP:
		state->setMaster_amp(value);
		state->sendParam("/module/live/paramc", "amp", "1", value);
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_PANFM3OP2_RATIO1:
		state->setPanFM3OP2_ratio1(value);
		state->sendParam("/module/live/paramc", "ratio1", "2", value);
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_PANFM3OP2_INDEX2:
		state->setPanFM3OP2_index2(value);
		state->sendParam("/module/live/paramc", "index2", "2", value);
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_PANFM3OP2_CAR_FREQ:
		state->setPanFM3OP2_car_freq(value);
		state->sendParam("/module/live/paramc", "car_freq", "2", value);
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

	
	case FMOD_OTHER_PARAM_PANFM3OP2_RATIO1:
		*value = state->PanFM3OP2_ratio1();
		if (valuestr) sprintf(valuestr, "% fl", state->PanFM3OP2_ratio1());
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_PANFM3OP2_INDEX2:
		*value = state->PanFM3OP2_index2();
		if (valuestr) sprintf(valuestr, "% fl", state->PanFM3OP2_index2());
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_PANFM3OP2_CAR_FREQ:
		*value = state->PanFM3OP2_car_freq();
		if (valuestr) sprintf(valuestr, "% fl", state->PanFM3OP2_car_freq());
		return FMOD_OK;

	/// ==============================================
	}

    return FMOD_ERR_INVALID_PARAM;
}
