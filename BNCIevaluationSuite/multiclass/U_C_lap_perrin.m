
% Copyright (C) 2003  Josep Marco for Starlab BCN SL
% modifications 2008 by Ivan Cester  for Starlab BCN SL:
%       -calculates the Laplacian of a temporal serie instead of a sample
%       -eliminate the plot
%       -the input chanlocs and substitute X, Y, Z for channel_location
%
% This program is free software; you can redistribute it and/or modify
% it under the terms of the GNU General Public License as published by
% the Free Software Foundation; either version 2 of the License, or
% (at your option) any later version.
%
% This program is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
% GNU General Public License for more details.
%
% You should have received a copy of the GNU General Public License
% along with this program; if not, write to the Free Software
% Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA


function lap=U_C_lap_perrin(voltage,channel_location,n_elec);

% Computes the laplacian of a given data following the algorithm developed by Perrin and Pernier. See the
% article Perrin, F., Pernier, J., Bertrand, O., and Echallier, J.F. (1989) Spherical splines for scalp potential 
% and current density mapping. Electroencephalogr. Clin. Neurophysiol., 72: 184-187
% INPUTS: voltage= voltatge data that we want to analyze matrix of dimensions (samples x channels);
%         X = array data containing the x position for the electrodes;
%         Y = array data containing the y position for the electrodes;
%         Z = array data containing the z position for the electrodes;
%         chanlocs = structure containing the position of electrodes (EEGLAB format). May also be an archive (see topoplot.m);
%         n_elec = number of electrodes;
%         m = order of splines (m=2 by default)
% OUTPUTS: lap = laplacian in the given electrodes


% Contributions:

% Nov 2002: Josep Marco ->first version
% March 2003: Josep Marco ->First EEGLAB version. Include plugin



pos_elec=channel_location; % makes a (n_elec,3) array containing the position of the electrodes

degree=20;
m=2;



mat=mat_g(n_elec,m,pos_elec,degree);  % computes the matrix with the splines related to the position between electrodes 
inv_mat=inv(mat);
for t=1:length(voltage(:,1))
    new_voltage=voltage(t,:);
    new_voltage(1,n_elec+1)=0.;    %
    c=inv_mat*new_voltage'; % These are the coefficients that interpolates the data
    for i=1:n_elec  
        lap(i,t)=compute_lap(i,c,n_elec,pos_elec,m,degree);   % compute the laplacian in the position of the electrodes
    end
end
lap=lap';
%figure;
%topoplot(-lap',chanlocs);   % draw the laplacian using the function topoplot available in EEGLAB.
