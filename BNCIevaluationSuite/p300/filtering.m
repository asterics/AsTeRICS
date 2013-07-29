function [xappf,xtestf]=filtering(xapp,xtest,triallength,NbChannel,N,Fc1,Fc2,Fe,decim);

% USAGE
%
% [xappf,xtestf]=filtering(xapp,xtest,triallength,NbChannel,N,Fc1,Fc2,Fe,decim);
%
%  filtering and decimates each signal channel in xapp and xtest
%  triallength      denotes the length of the signal in each channel
%  NbChannel        number of channel in xapp(i,:) 
%  N                filter order
%  Fc1,Fc2          cut-off frequencies
%  Fe               Sampling frequency
%  decim            binary for performing decimation

% 16/05/2005

R=0.5;
W=[2*Fc1/Fe 2*Fc2/Fe];
if sum(W>1)>0
    error('Bad frequency cut');
end;
[b,a]=cheby1(N,R,W);


xappf=[];
xtestf=[];
nbapp=size(xapp,1);
for i=1:nbapp
    aux=[];
    for j=1:NbChannel
        filt=filter(b,a,xapp(i,(j-1)*triallength+1: j*triallength));
        
        if  ~decim
             aux=[aux filt ];
         else
           
             filt=decimate(filt,round(Fe/Fc2));
             aux=[aux filt ];
        end;
    end;
    xappf=[xappf;aux];
end;

nbtest=size(xtest,1);
for i=1:nbtest
    aux=[];
    for j=1:NbChannel
         filt=filter(b,a,xtest(i,(j-1)*triallength+1: j*triallength));
        if  ~decim
             aux=[aux filt ];
         else
             
             filt=decimate(filt,round(Fe/Fc2));
             aux=[aux filt ];
        end;
        
    end;
    xtestf=[xtestf;aux];
end;
