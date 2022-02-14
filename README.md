# Block Infection
Terraform Minecraft worlds with an infection.

Block Infection is a **proof-of-concept** Minecraft Plugin that currently serves no purpose other than
to showcase what Minecraft is capable of.


Block Infection uses an algorithm similar to a Breadth-First-Search using Minecraft blocks as nodes.
It includes multiple terraformer implementations that each handle and spread the infection differently.
One algorithm simply erases encountered nodes which causes the terrain to "melt" as it spreads. Others freeze terrain
or even swap terrain with other worlds. Infections can be quarantined and paired against each-other to simulate
interesting scenarios.

# Usage

Block Infection comes with a single command to start an infection.

### /bi start

Infections are started at the target crosshair location.

- **destroy**
    - Start a block destroying infection. Infection will not travel through air or non-solid blocks.

- **transform** *fromBlock* *toBlock*
    - Convert one type of block into another. Infection will only spread through the *fromBlock* and will only create
      the *toBlock*.

