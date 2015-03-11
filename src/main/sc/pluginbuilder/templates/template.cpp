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
#include <iostream>
#include <string>

#include "../ip/UdpSocket.h"
#include "../osc/OscOutboundPacketStream.h"

#include "../inc/fmod.hpp"

#define ADDRESS "127.0.0.1"
#define PORT 57120

///=============================
%%%TOP_ROUTE%%%
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
	%%%PARAM_ENUMS%%%
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
FMOD_RESULT F_CALLBACK FMOD_Other_shouldiprocess  (FMOD_DSP_STATE *dsp_state, bool inputsidle, unsigned int length, FMOD_CHANNELMASK inmask, int inchannels, FMOD_SPEAKERMODE speakermode);


/// ==========================
%%%STATIC_PARAM_DESC%%%
/// ===========================

FMOD_DSP_PARAMETER_DESC *FMOD_Other_dspparam[FMOD_OTHER_NUM_PARAMETERS] =
{
	/// =====================================
	%%%PARAM_DESC_POINTERS%%%
	/// =====================================
};

FMOD_DSP_DESCRIPTION FMOD_Other_Desc =
{
    FMOD_PLUGIN_SDK_VERSION,
	/// =====================================
	%%%DESC_NAME%%%
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
    FMOD_Other_shouldiprocess,
    0
};

extern "C"
{

F_DECLSPEC F_DLLEXPORT FMOD_DSP_DESCRIPTION* F_STDCALL FMODGetDSPDescription()
{
	/// ====================================
	%%%PARAM_DESCRIPTIONS%%%
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
	%%%PARAM_CLASS_DECS%%%
	/// ================================

	void sendParam(const char *, const char *, const char *, float);
	void sendMsg(const char *, float);
	void setOSCID(int);

	int osc_id() const { return m_osc_id; }

private:

	/// =============================
	%%%PARAM_PRIVATE_DECS%%%
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

}

void FMODOtherState::reset()
{

}

/// =======================================
%%%PARAM_CLASS_FUNCTIONS%%%
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

	std::string address = TOP_ROUTE;
	address += addr;

	UdpTransmitSocket transmitSocket( IpEndpointName( ADDRESS, PORT ) );
    
    char buffer[OUTPUT_BUFFER_SIZE];
    osc::OutboundPacketStream p( buffer, OUTPUT_BUFFER_SIZE );
    
    p << osc::BeginBundleImmediate
        << osc::BeginMessage( address.c_str() )
            << value << osc::EndMessage
        << osc::EndBundle;
    
    transmitSocket.Send( p.Data(), p.Size() );
}

FMOD_RESULT F_CALLBACK FMOD_Other_dspcreate(FMOD_DSP_STATE *dsp_state)
{
    dsp_state->plugindata = (FMODOtherState *)FMOD_DSP_STATE_MEMALLOC(dsp_state, sizeof(FMODOtherState), FMOD_MEMORY_NORMAL, "FMODOtherState");

	FMODOtherState *state = (FMODOtherState *)dsp_state->plugindata;

    if (!dsp_state->plugindata)
    {
        return FMOD_ERR_MEMORY;
    }
    return FMOD_OK;
}

FMOD_RESULT F_CALLBACK FMOD_Other_dsprelease(FMOD_DSP_STATE *dsp_state)
{
    FMODOtherState *state = (FMODOtherState *)dsp_state->plugindata;
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
	%%%SET_PARAM_FLOAT%%%
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
	%%%GET_PARAM_FLOAT%%%
	/// ==============================================
	}

    return FMOD_ERR_INVALID_PARAM;
}

FMOD_RESULT F_CALLBACK FMOD_Other_shouldiprocess(FMOD_DSP_STATE * /*dsp_state*/, bool inputsidle, unsigned int /*length*/, FMOD_CHANNELMASK /*inmask*/, int /*inchannels*/, FMOD_SPEAKERMODE /*speakermode*/)
{
    if (inputsidle)
    {
        //return FMOD_ERR_DSP_DONTPROCESS;
		return FMOD_OK;
    }

    return FMOD_OK;
}