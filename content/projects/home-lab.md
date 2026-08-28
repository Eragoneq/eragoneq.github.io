---
name: HomeLab
description: Setting up my own infrastructure for everyday computing use
date: 2026-08-28
last_update: 2026-08-28
status: Active
---

# HomeLab

## Introduction

In the modern day and age, when everything is digital, it's hard to keep everything under control.
Most of the stuff we use is based on the services provided by the companies, where they can decide on a whim that they won't provide you the service anymore.

This is especially important now, as most of the free tier stuff is even more lucrative, because of all the access to the data that's used for training AI models.

As such for the sake of ownership of my data, privacy, sovereignty and of course for fun, I have prepared my own HomeLab. 

## Hardware

Currently, the stack mainly consists of a few small servers:

* Small Dell Optiplex PC
* Old Dell PowerEdge server
* Raspberry Pi 5
# 
This is enough to run most of the basic software that is accessible to 1 user, for now, but it should be able to handle a few users locally with no issues.


I can definitely recommend getting the deals on Optiplexes or other mini PCs that are due to be discarded, as that's the best deal you can get and allow for easy experimenting with a low budget.

## Software

For now the most important elements of the lab include:
- **Tailscale** for easy P2P access without exposing the network
- **Forgejo** as the local git server, for all the random experiments and projects that are not good enough to publish
- **Grafana + VictoriaMetrics** for monitoring status of all the machines
- **Ansible + Semaphore UI** for quick and easy maintenance and scheduled jobs work on the machines

## Main use cases

==TBA==

